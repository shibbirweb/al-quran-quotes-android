package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shibbir.me.alquranquotes.data.repository.QuoteRepository
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.navigation.AppDestination
import javax.inject.Inject

/** Adds a new quote of the user's own, or edits any quote whose id navigation passed in. */
@HiltViewModel
class QuoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quoteRepository: QuoteRepository,
    private val quoteDraftValidator: QuoteDraftValidator,
) : ViewModel() {

    /** The quote being edited, or null when adding a new one. */
    private val quoteId: Long? =
        savedStateHandle.get<Long>(AppDestination.EditQuote.QUOTE_ID_KEY)

    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<QuoteEditorUiState> = _uiState.asStateFlow()

    init {
        if (quoteId != null) {
            loadQuoteToEdit(quoteId)
        }
    }

    /**
     * Switches the form to [quoteKind], keeping what was typed in every field. Only when adding:
     * an edited quote keeps its kind.
     */
    fun selectQuoteKind(quoteKind: QuoteKind) {
        if (_uiState.value.isEditing) {
            return
        }
        changeForm { form -> form.copy(quoteKind = quoteKind) }
    }

    /** Stores [fieldText] as the new text of [quoteEditorField]. */
    fun updateField(quoteEditorField: QuoteEditorField, fieldText: String) {
        changeForm { form -> form.withFieldText(quoteEditorField, fieldText) }
    }

    /** Saves the form when it is valid, or shows the error of each invalid field. */
    fun save() {
        val editorState = _uiState.value
        if (!editorState.canSave) {
            return
        }
        val validationResult = quoteDraftValidator.validate(editorState.form)
        when (validationResult) {
            is QuoteValidationResult.Valid -> saveDraft(validationResult.quoteDraft)
            is QuoteValidationResult.Invalid -> showFieldErrors(validationResult.fieldErrors)
        }
    }

    private fun initialUiState(): QuoteEditorUiState {
        if (quoteId == null) {
            return QuoteEditorUiState(isEditing = false, status = QuoteEditorStatus.READY)
        }
        return QuoteEditorUiState(isEditing = true, status = QuoteEditorStatus.LOADING)
    }

    private fun loadQuoteToEdit(quoteId: Long) {
        viewModelScope.launch {
            val quoteDraft = loadQuoteDraftOrNull(quoteId)
            showLoadedQuote(quoteDraft)
        }
    }

    /** Returns null when the quote does not exist or cannot be read. */
    private suspend fun loadQuoteDraftOrNull(quoteId: Long): QuoteDraft? {
        try {
            return quoteRepository.getQuoteDraft(quoteId)
        } catch (exception: Exception) {
            // Rethrows only when this ViewModel itself was cleared while loading.
            currentCoroutineContext().ensureActive()
            return null
        }
    }

    private fun showLoadedQuote(quoteDraft: QuoteDraft?) {
        if (quoteDraft == null) {
            _uiState.update { editorState ->
                editorState.copy(status = QuoteEditorStatus.UNAVAILABLE)
            }
            return
        }
        val loadedForm = quoteDraft.toQuoteEditorForm()
        _uiState.update { editorState ->
            editorState.copy(status = QuoteEditorStatus.READY, form = loadedForm)
        }
    }

    private fun changeForm(formChange: (QuoteEditorForm) -> QuoteEditorForm) {
        _uiState.update { editorState ->
            val changedForm = formChange(editorState.form)
            val fieldErrors = fieldErrorsToShow(editorState.hasAttemptedSave, changedForm)
            editorState.copy(form = changedForm, fieldErrors = fieldErrors)
        }
    }

    private fun fieldErrorsToShow(
        hasAttemptedSave: Boolean,
        form: QuoteEditorForm,
    ): Map<QuoteEditorField, QuoteFieldError> {
        if (!hasAttemptedSave) {
            return emptyMap()
        }
        return quoteDraftValidator.fieldErrorsOf(form)
    }

    private fun showFieldErrors(fieldErrors: Map<QuoteEditorField, QuoteFieldError>) {
        _uiState.update { editorState ->
            editorState.copy(fieldErrors = fieldErrors, hasAttemptedSave = true)
        }
    }

    private fun saveDraft(quoteDraft: QuoteDraft) {
        _uiState.update { editorState ->
            editorState.copy(isSaving = true, hasSaveFailed = false)
        }
        viewModelScope.launch {
            val isSaved = tryWriteDraft(quoteDraft)
            _uiState.update { editorState ->
                editorState.copy(isSaving = false, isSaved = isSaved, hasSaveFailed = !isSaved)
            }
        }
    }

    /** Returns false when saving failed. */
    private suspend fun tryWriteDraft(quoteDraft: QuoteDraft): Boolean {
        try {
            writeDraft(quoteDraft)
            return true
        } catch (exception: Exception) {
            // Rethrows only when this ViewModel itself was cleared while saving.
            currentCoroutineContext().ensureActive()
            return false
        }
    }

    /** Adds a new quote, or updates the one being edited. */
    private suspend fun writeDraft(quoteDraft: QuoteDraft) {
        val editedQuoteId = quoteId
        if (editedQuoteId == null) {
            quoteRepository.addQuote(quoteDraft)
            return
        }
        quoteRepository.updateQuote(editedQuoteId, quoteDraft)
    }
}

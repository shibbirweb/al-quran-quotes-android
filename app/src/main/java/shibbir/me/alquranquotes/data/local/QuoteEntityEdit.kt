package shibbir.me.alquranquotes.data.local

import shibbir.me.alquranquotes.model.QuoteDraft

/**
 * This row with the fields of [quoteDraft]. It keeps its id, kind, and bundled key, and its
 * origin moves on as [afterEdit] says, so an edited bundled ayah is never overwritten by a seed.
 *
 * @throws IllegalArgumentException when [quoteDraft] is of the other kind than this row.
 */
internal fun QuoteEntity.editedWith(quoteDraft: QuoteDraft): QuoteEntity {
    val editedOrigin = origin.afterEdit()
    val editedQuoteEntity = quoteDraft.toQuoteEntity(origin = editedOrigin)
    require(editedQuoteEntity.type == type) {
        "Quote $id is of type $type and cannot become ${editedQuoteEntity.type}"
    }
    return editedQuoteEntity.copy(
        id = id,
        bundledKey = bundledKey,
    )
}

package shibbir.me.alquranquotes.model

/** Where a quote came from. Every origin can be edited and deleted. */
enum class QuoteOrigin {
    /** An ayah bundled with the app that the user has not changed. */
    BUNDLED,

    /** A bundled ayah the user edited. App updates never overwrite it. */
    EDITED_BUNDLED,

    /** A quote the user added. */
    USER,
}

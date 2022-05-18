package com.example.serversmsretrieverbi.modelo

import com.google.firebase.Timestamp
import kotlin.collections.ArrayList

data class ListCode(
        var listaCode: MutableList<OTPs>? = null
)
data class OTPs(
    var code: Int? = null,
    var numtel: String? = null,
    var expedicion: Timestamp? = null,
    var caducidad: String? = null
)

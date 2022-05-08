package com.example.serversmsretrieverbi.modelo

import com.google.firebase.Timestamp
import kotlin.collections.ArrayList

data class ListCode(
        var listaCode: MutableList<Clave>? = null
)
data class Clave(
    var code: Int? = null,
    var numtel: String? = null,
    var expedicion: Timestamp? = null
)

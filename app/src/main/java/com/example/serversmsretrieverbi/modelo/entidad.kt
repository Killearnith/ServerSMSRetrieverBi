package com.example.serversmsretrieverbi.modelo

import com.google.firebase.Timestamp

data class ListCode(
        var listaCode: MutableList<Clave>? = null
)
data class Clave(
    var code: String? = null,
    var numtel: String? = null,
    var expiracion: Timestamp? = null
)

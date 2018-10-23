package org.docksidestage.app.web.lido.mypage

import org.docksidestage.dbflute.exentity.Product
import org.lastaflute.web.validation.Required

/**
 * @author jflute
 */
class MypageProductResult(product: Product) {

    @Required
    val productName: String
    @Required
    val regularPrice: Int?

    init {
        this.productName = product.productName
        this.regularPrice = product.regularPrice
    }

    override fun toString(): String {
        return "{$productName, $regularPrice}"
    }
}
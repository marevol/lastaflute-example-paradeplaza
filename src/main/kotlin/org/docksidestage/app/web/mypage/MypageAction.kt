/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.app.web.mypage

import org.dbflute.cbean.result.ListResultBean
import org.docksidestage.app.web.base.ParadeplazaBaseAction
import org.docksidestage.dbflute.exbhv.ProductBhv
import org.docksidestage.dbflute.exentity.Product
import org.docksidestage.mylasta.action.ParadeplazaHtmlPath
import org.lastaflute.web.Execute
import org.lastaflute.web.response.HtmlResponse
import javax.annotation.Resource

/**
 * @author jflute
 */
class MypageAction : ParadeplazaBaseAction() {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private lateinit var productBhv: ProductBhv

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    fun index(): HtmlResponse {
        val recentProducts = mappingToProducts(selectRecentProductList())
        val highPriceProducts = mappingToProducts(selectHighPriceProductList())
        return asHtml(ParadeplazaHtmlPath.path_Mypage_MypageHtml).renderWith { data ->
            data.register("recentProducts", recentProducts)
            data.register("highPriceProducts", highPriceProducts)
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private fun selectRecentProductList(): ListResultBean<Product> {
        return productBhv.selectList { cb ->
            cb.specify().derivedPurchase().max({ purchaseCB -> purchaseCB.specify().columnPurchaseDatetime() }, Product.ALIAS_latestPurchaseDate)
            cb.query().existsPurchase { purchaseCB -> purchaseCB.query().setMemberId_Equal(userBean.get().memberId) }
            cb.query().addSpecifiedDerivedOrderBy_Desc(Product.ALIAS_latestPurchaseDate)
            cb.query().addOrderBy_ProductId_Asc()
            cb.fetchFirst(3)
        }
    }

    private fun selectHighPriceProductList(): ListResultBean<Product> {
        return productBhv.selectList { cb ->
            cb.query().existsPurchase { purchaseCB -> purchaseCB.query().setMemberId_Equal(userBean.get().memberId) }
            cb.query().addOrderBy_RegularPrice_Desc()
            cb.fetchFirst(3)
        }
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private fun mappingToProducts(productList: List<Product>): List<MypageProductBean> {
        return productList.map { product -> MypageProductBean(product) }
    }
}

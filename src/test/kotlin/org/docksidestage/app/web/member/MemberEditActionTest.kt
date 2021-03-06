package org.docksidestage.app.web.member

import org.docksidestage.dbflute.allcommon.CDef
import org.docksidestage.dbflute.exbhv.MemberBhv
import org.docksidestage.mylasta.action.ParadeplazaHtmlPath
import org.docksidestage.unit.UnitParadeplazaTestCase
import org.junit.Assert
import org.lastaflute.web.token.exception.DoubleSubmittedRequestException
import java.time.LocalDate
import javax.annotation.Resource

/**
 * @author jflute
 */
class MemberEditActionTest : UnitParadeplazaTestCase() {

    @Resource
    private lateinit var memberBhv: MemberBhv

    // ===================================================================================
    //                                                                             index()
    //                                                                             =======
    fun test_index_success() {
        // ## Arrange ##
        val action = MemberEditAction()
        inject(action)
        val memberId = 1
        val member = memberBhv.selectByPK(memberId).get()

        // ## Act ##
        val response = action.index(memberId)

        // ## Assert ##
        val htmlData = validateHtmlData(response)
        htmlData.assertHtmlForward(ParadeplazaHtmlPath.path_Member_MemberEditHtml)
        val form = htmlData.requiredPushedForm(MemberEditForm::class.java)
        Assert.assertEquals(member.memberName, form.memberName)
        assertTokenSaved(action.javaClass)
    }

    // ===================================================================================
    //                                                                            update()
    //                                                                            ========
    fun test_update_success() {
        // ## Arrange ##
        val action = MemberEditAction()
        inject(action)
        mockTokenRequested(action.javaClass)
        val form = prepareEditForm()

        // ## Act ##
        val response = action.update(form)

        // ## Assert ##
        val htmlData = validateHtmlData(response)
        htmlData.assertRedirect(action.javaClass)
        assertTokenVerified()
    }

    fun test_update_doubleSubmitted() {
        // ## Arrange ##
        val action = MemberEditAction()
        inject(action)
        mockTokenRequestedAsDoubleSubmit(action.javaClass)
        val form = prepareEditForm()

        // ## Act ##
        // ## Assert ##
        assertException(DoubleSubmittedRequestException::class.java) { action.update(form) }
    }

    private fun prepareEditForm(): MemberEditForm {
        val memberId = 1
        val member = memberBhv.selectByPK(memberId).get()

        val form = MemberEditForm()
        form.memberId = memberId
        form.memberName = "sea"
        form.memberAccount = "land"
        form.memberStatus = CDef.MemberStatus.Provisional
        form.birthdate = LocalDate.of(2016, 12, 2)
        form.versionNo = member.versionNo
        form.previousStatus = member.memberStatusCodeAsMemberStatus
        return form
    }
}

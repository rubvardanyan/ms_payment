package com.sfl.pms.services.payment.redirect.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.acapture.AcaptureRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.impl.AbstractPaymentProviderRedirectResultServiceImplTest;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.apache.commons.lang3.SerializationUtils;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.fail;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:17 PM
 */
public class AcaptureRedirectResultServiceImplTest extends AbstractPaymentProviderRedirectResultServiceImplTest<AcaptureRedirectResult> {

    /* Test subject and mocks */
    @TestSubject
    private AcaptureRedirectResultServiceImpl acaptureRedirectResultService = new AcaptureRedirectResultServiceImpl();

    @Mock
    private AcaptureRedirectResultRepository acaptureRedirectResultRepository;

    /* Constructor */
    public AcaptureRedirectResultServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testCreatePaymentProviderRedirectResultWithInvalidArguments() {
        // Test data
        final AcaptureRedirectResultDto validDto = getServicesImplTestHelper().createAcaptureRedirectResultDto();
        AcaptureRedirectResultDto invalidDto;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            invalidDto = null;
            acaptureRedirectResultService.createPaymentProviderRedirectResult(invalidDto);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        try {
            invalidDto = SerializationUtils.clone(validDto);
            invalidDto.setCheckoutId(null);
            acaptureRedirectResultService.createPaymentProviderRedirectResult(invalidDto);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        try {
            invalidDto = SerializationUtils.clone(validDto);
            invalidDto.setResourcePath(null);
            acaptureRedirectResultService.createPaymentProviderRedirectResult(invalidDto);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderRedirectResult() {
        // Test data
        final AcaptureRedirectResultDto acaptureRedirectResultDto = getServicesImplTestHelper().createAcaptureRedirectResultDto();
        // Reset
        resetAll();
        // Expectations
        expect(acaptureRedirectResultRepository.save(isA(AcaptureRedirectResult.class))).andAnswer(() -> (AcaptureRedirectResult)getCurrentArguments()[0]);
        // Replay
        replayAll();
        // Run test scenario
        final AcaptureRedirectResult result = acaptureRedirectResultService.createPaymentProviderRedirectResult(acaptureRedirectResultDto);
        getServicesImplTestHelper().assertAcaptureRedirectResult(result, acaptureRedirectResultDto);
        // Verify
        verifyAll();
    }

    /* Override methods */
    @Override
    protected AbstractPaymentProviderRedirectResultRepository<AcaptureRedirectResult> getRepository() {
        return acaptureRedirectResultRepository;
    }

    @Override
    protected AbstractPaymentProviderRedirectResultService<AcaptureRedirectResult> getService() {
        return acaptureRedirectResultService;
    }

    @Override
    protected AcaptureRedirectResult getInstance() {
        return getServicesImplTestHelper().createAcaptureRedirectResult();
    }

    @Override
    protected Class<AcaptureRedirectResult> getInstanceClass() {
        return AcaptureRedirectResult.class;
    }
}
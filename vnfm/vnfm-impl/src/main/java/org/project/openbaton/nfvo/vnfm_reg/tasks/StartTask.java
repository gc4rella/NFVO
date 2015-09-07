package org.project.openbaton.nfvo.vnfm_reg.tasks;

import org.project.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.project.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.project.openbaton.nfvo.vnfm_reg.tasks.abstracts.AbstractTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by lto on 06/08/15.
 */
@Service
@Scope("prototype")
public class StartTask extends AbstractTask {

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void doWork() throws Exception {
        log.debug("----> STARTED VNFR: " + virtualNetworkFunctionRecord.getName());
        log.debug("vnfr arrived version= "+virtualNetworkFunctionRecord.getHb_version());

        VirtualNetworkFunctionRecord existingvnfr = vnfrRepository.findOne(virtualNetworkFunctionRecord.getId());
        log.debug("vnfr existing version= "+existingvnfr.getHb_version());
        log.debug("try to merge vnfr arrived");
        virtualNetworkFunctionRecord= vnfrRepository.save(virtualNetworkFunctionRecord);
    }
}

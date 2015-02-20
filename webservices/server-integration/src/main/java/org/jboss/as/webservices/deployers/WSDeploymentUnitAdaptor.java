package org.jboss.as.webservices.deployers;

import java.util.List;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.AttachmentList;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;

public class WSDeploymentUnitAdaptor implements WSDeploymentUnit {

    private final DeploymentUnit du;

    public WSDeploymentUnitAdaptor(DeploymentUnit du) {
        this.du = du;
    }

    @Override
    public boolean hasAttachment(AttachmentKey<?> key) {
        return du.hasAttachment(key);
    }

    @Override
    public <T> T getAttachment(AttachmentKey<T> key) {
        return du.getAttachment(key);
    }

    @Override
    public <T> T putAttachment(AttachmentKey<T> key, T value) {
        return du.putAttachment(key, value);
    }

    @Override
    public String getName() {
        return du.getName();
    }

    @Override
    public WSDeploymentUnitAdaptor getParent() {
        return new WSDeploymentUnitAdaptor(du.getParent());
    }

    @Override
    public ServiceName getServiceName() {
        return du.getServiceName();
    }

    @Override
    public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
        return du.getAttachmentList(key);
    }

    @Override
    public <T> T removeAttachment(AttachmentKey<T> key) {
        return du.removeAttachment(key);
    }

    @Override
    public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
        du.addToAttachmentList(key, value);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return du.getServiceRegistry();
    }
}

package org.jboss.as.webservices.deployers;

import org.jboss.as.server.deployment.Attachable;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;

public interface WSDeploymentUnit extends Attachable {

    /**
     * Get the service name of the root deployment unit service.
     *
     * @return the service name
     */
    ServiceName getServiceName();

    /**
     * Get the simple name of the deployment unit.
     *
     * @return the simple name
     */
    String getName();

    /**
     * Get the deployment unit of the parent (enclosing) deployment.
     *
     * @return the parent deployment unit, or {@code null} if this is a top-level deployment
     */
    WSDeploymentUnit getParent();

    /**
     * Get the service registry.
     *
     * @return the service registry
     */
    ServiceRegistry getServiceRegistry();
}

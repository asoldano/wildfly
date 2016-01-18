/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jaxrs;

import java.lang.ref.WeakReference;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.wsf.spi.classloading.JAXRSClassLoaderProvider;

/**
 * WildFly version of {@link org.jboss.wsf.spi.classloading.JAXRSClassLoaderProvider}, relying on modular classloading.
 *
 * @author alessio.soldano@jboss.com
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
public class ModuleJAXRSClassLoaderProvider extends JAXRSClassLoaderProvider {

    private static final ModuleIdentifier ASIL = ModuleIdentifier.create("org.jboss.as.webservices.server.integration"); //TODO this might not be there in slim distribution (without JAXWS) ?
    private WeakReference<ClassLoader> integrationClassLoader;

    @Override
    public ClassLoader getJAXRSSubsystemClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    public ClassLoader getServerIntegrationClassLoader() {
        if (integrationClassLoader == null || integrationClassLoader.get() == null) {
            try {
                Module module = Module.getBootModuleLoader().loadModule(ASIL);
                integrationClassLoader = new WeakReference<ClassLoader>(module.getClassLoader());
            } catch (ModuleLoadException e) {
                throw new RuntimeException(e);
            }
        }
        return integrationClassLoader.get();
    }

    public static void register() {
        JAXRSClassLoaderProvider.setDefaultProvider(new ModuleJAXRSClassLoaderProvider());
    }

}

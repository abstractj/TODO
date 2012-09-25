/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.todo.server.util;

import org.aerogear.todo.server.security.idm.AeroGearCredential;
import org.jboss.picketlink.cdi.Identity;
import org.picketbox.cdi.PicketBoxUser;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
public class HttpResponseBuilder {

    @Inject
    private Identity identity;

    /**
     * Note: is not recommendable to return username and roles here
     * It will be discussed on M7
     * @return
     */
    public AeroGearCredential createResponse() {

        AeroGearCredential aeroGearCredential = null;

        if (identity.isLoggedIn()) {
            PicketBoxUser user = (PicketBoxUser) identity.getUser();
            String token = user.getSubject().getSession().getId().getId().toString();
            List<String> roles = user.getSubject().getRoleNames();
            aeroGearCredential = new AeroGearCredential(user.getId(), token, "true", roles);
        }

        return aeroGearCredential;
    }

}

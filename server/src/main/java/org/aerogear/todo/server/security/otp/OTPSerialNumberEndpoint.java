/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.aerogear.todo.server.security.otp;

import org.aerogear.todo.server.security.idm.UserInfo;
import org.aerogear.todo.server.util.Base32;
import org.picketbox.cdi.PicketBoxIdentity;
import org.picketbox.core.UserContext;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;

/**
 * Obtain the serial number for OTP clients
 * @author anil saldhana
 * @since Oct 9, 2012
 */
@Stateless
@Path("/otpserial")
public class OTPSerialNumberEndpoint {
    @Inject
    private PicketBoxIdentity identity;
    @Inject
    private IdentityManager identityManager;
    
    @GET
    @Produces (MediaType.APPLICATION_JSON)
    public UserInfo getInfo() {
        UserInfo userInfo = new UserInfo();
        
        UserContext userContext = identity.getUserContext();
        
        User user = userContext.getUser();
        
        userInfo.setUserId(user.getKey());
        userInfo.setFullName(user.getFullName());
        
        Collection<Role> roles = userContext.getRoles();
        String[] rolesArray = new String[roles.size()];
        
        int i = 0;
        
        for (Role role : roles) {
            rolesArray[i] = role.getName();
            i++;
        }
        
        userInfo.setRoles(rolesArray);
        
        User idmuser = identityManager.getUser(user.getKey());
        String serialNumber = idmuser.getAttribute("serial");
        if(serialNumber == null){
            //Generate serial number
            serialNumber = UUID.randomUUID().toString();
            serialNumber = serialNumber.replace('-', 'c');
            
            //Just pick the first 10 characters
            serialNumber = serialNumber.substring(0, 10);
            idmuser.setAttribute("serial", serialNumber);
        }
        userInfo.setSerial(serialNumber);
        userInfo.setB32(Base32.encode(serialNumber.getBytes()));
        
        return userInfo;
    }
}
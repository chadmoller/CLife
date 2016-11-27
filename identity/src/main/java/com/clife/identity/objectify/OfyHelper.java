/**
 * Copyright 2014-2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//[START all]
package com.clife.identity.objectify;

import com.clife.identity.Greeting;
import com.clife.identity.Guestbook;
import com.clife.identity.domain.Authority;
import com.clife.identity.domain.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;
import java.util.HashSet;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a JSP is run.  This is
 * required to let JSP's access Ofy.
 **/
public class OfyHelper implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user request if no warmup
        // request.
        ObjectifyService.register(Guestbook.class);
        ObjectifyService.register(Greeting.class);
        ObjectifyService.register(User.class);

        try (Closeable closeable = ObjectifyService.begin()) {
            Objectify objectify = ObjectifyService.ofy();

            User user = objectify.load()
                    .type(User.class)
                    .filter("userName", "chad.moller@gmail.com")
                    .first()
                    .now();

            if (user == null) {
                System.out.println("Creating my root user");
                user = new User();
                user.email = "chad.moller@gmail.com";
                user.birthdate = new Date(1978, 10, 28);
                user.enabled = true;
                user.nickname = "Chad";
                user.fullName = "Chad Moller";
                user.authorities = new HashSet<>();
                user.authorities.add(Authority.ROOT);
                objectify.save().entity(user).now();
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}
//[END all]

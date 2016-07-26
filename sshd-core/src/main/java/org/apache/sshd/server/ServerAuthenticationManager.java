/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sshd.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.auth.BuiltinUserAuthFactories;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.gss.GSSAuthenticator;
import org.apache.sshd.server.auth.gss.UserAuthGSSFactory;
import org.apache.sshd.server.auth.hostbased.HostBasedAuthenticator;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.auth.keyboard.UserAuthKeyboardInteractiveFactory;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;

/**
 * Holds providers and helpers related to the server side authentication process
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public interface ServerAuthenticationManager {
    /**
     * Key used to retrieve the value in the configuration properties map
     * of the maximum number of failed authentication requests before the
     * server closes the connection.
     * @see #DEFAULT_MAX_AUTH_REQUESTS
     */
    String MAX_AUTH_REQUESTS = "max-auth-requests";

    /**
     * Default value for {@link #MAX_AUTH_REQUESTS} if none configured
     */
    int DEFAULT_MAX_AUTH_REQUESTS = 20;

    /**
     * Retrieve the list of named factories for <code>UserAuth</code> objects.
     *
     * @return a list of named <code>UserAuth</code> factories, never {@code null}/empty
     */
    List<NamedFactory<UserAuth>> getUserAuthFactories();
    default String getUserAuthFactoriesNameList() {
        return NamedResource.Utils.getNames(getUserAuthFactories());
    }
    default List<String> getUserAuthFactoriesNames() {
        return NamedResource.Utils.getNameList(getUserAuthFactories());
    }

    void setUserAuthFactories(List<NamedFactory<UserAuth>> userAuthFactories);
    default void setUserAuthFactoriesNameList(String names) {
        setUserAuthFactoriesNames(GenericUtils.split(names, ','));
    }
    default void setUserAuthFactoriesNames(String ... names) {
        setUserAuthFactoriesNames(GenericUtils.isEmpty((Object[]) names) ? Collections.<String>emptyList() : Arrays.asList(names));
    }
    default void setUserAuthFactoriesNames(Collection<String> names) {
        BuiltinUserAuthFactories.ParseResult result = BuiltinUserAuthFactories.parseFactoriesList(names);
        @SuppressWarnings({ "rawtypes", "unchecked" })
        List<NamedFactory<UserAuth>> factories =
                (List) ValidateUtils.checkNotNullAndNotEmpty(result.getParsedFactories(), "No supported cipher factories: %s", names);
        Collection<String> unsupported = result.getUnsupportedFactories();
        ValidateUtils.checkTrue(GenericUtils.isEmpty(unsupported), "Unsupported cipher factories found: %s", unsupported);
        setUserAuthFactories(factories);
    }

    /**
     * Retrieve the <code>PublickeyAuthenticator</code> to be used by SSH server.
     * If no authenticator has been configured (i.e. this method returns
     * {@code null}), then client authentication requests based on keys will be
     * rejected.
     *
     * @return the {@link PublickeyAuthenticator} or {@code null}
     */
    PublickeyAuthenticator getPublickeyAuthenticator();
    void setPasswordAuthenticator(PasswordAuthenticator passwordAuthenticator);

    /**
     * Retrieve the <code>PasswordAuthenticator</code> to be used by the SSH server.
     * If no authenticator has been configured (i.e. this method returns
     * {@code null}), then client authentication requests based on passwords
     * will be rejected.
     *
     * @return the {@link PasswordAuthenticator} or {@code null}
     */
    PasswordAuthenticator getPasswordAuthenticator();
    void setPublickeyAuthenticator(PublickeyAuthenticator publickeyAuthenticator);

    /**
     * Retrieve the <code>KeyboardInteractiveAuthenticator</code> to be used by
     * the SSH server. If no authenticator has been configured (i.e. this method returns
     * {@code null}), then client authentication requests based on this method
     * will be rejected.
     *
     * @return The {@link KeyboardInteractiveAuthenticator} or {@code null}
     */
    KeyboardInteractiveAuthenticator getKeyboardInteractiveAuthenticator();
    void setKeyboardInteractiveAuthenticator(KeyboardInteractiveAuthenticator interactiveAuthenticator);

    /**
     * Retrieve the <code>GSSAuthenticator</code> to be used by the SSH server.
     * If no authenticator has been configured (i.e. this method returns
     * {@code null}), then client authentication requests based on gssapi
     * will be rejected.
     *
     * @return the {@link GSSAuthenticator} or {@code null}
     */
    GSSAuthenticator getGSSAuthenticator();
    void setGSSAuthenticator(GSSAuthenticator gssAuthenticator);

    /**
     * Retrieve the {@code HostBasedAuthenticator} to be used by the SSH server. If
     * no authenticator has been configured (i.e. this method returns {@code null}),
     * then client authentication requests based on this method will be rejected.
     *
     * @return the {@link HostBasedAuthenticator} or {@code null}
     */
    HostBasedAuthenticator getHostBasedAuthenticator();
    void setHostBasedAuthenticator(HostBasedAuthenticator hostBasedAuthenticator);

    // CHECKSTYLE:OFF
    final class Utils {
    // CHECKSTYLE:ON
        public static final UserAuthPublicKeyFactory DEFAULT_USER_AUTH_PUBLIC_KEY_FACTORY =
                UserAuthPublicKeyFactory.INSTANCE;

        public static final UserAuthGSSFactory DEFAULT_USER_AUTH_GSS_FACTORY =
                UserAuthGSSFactory.INSTANCE;

        public static final UserAuthPasswordFactory DEFAULT_USER_AUTH_PASSWORD_FACTORY =
                UserAuthPasswordFactory.INSTANCE;

        public static final UserAuthKeyboardInteractiveFactory DEFAULT_USER_AUTH_KB_INTERACTIVE_FACTORY =
                UserAuthKeyboardInteractiveFactory.INSTANCE;

        private Utils() {
            throw new UnsupportedOperationException("No instance allowed");
        }

        /**
         * If user authentication factories already set, then simply returns them. Otherwise,
         * builds the factories list from the individual authenticators available for
         * the manager - password public key, keyboard-interactive, GSS, etc...
         *
         * @param manager The {@link ServerAuthenticationManager} - ignored if {@code null}
         * @return The resolved {@link List} of {@link NamedFactory} for the {@link UserAuth}s
         * @see #resolveUserAuthFactories(ServerAuthenticationManager, List)
         */
        public static List<NamedFactory<UserAuth>> resolveUserAuthFactories(ServerAuthenticationManager manager) {
            if (manager == null) {
                return Collections.emptyList();
            } else {
                return resolveUserAuthFactories(manager, manager.getUserAuthFactories());
            }
        }

        /**
         * If user authentication factories already set, then simply returns them. Otherwise,
         * builds the factories list from the individual authenticators available for
         * the manager - password public key, keyboard-interactive, GSS, etc...
         *
         * @param manager The {@link ServerAuthenticationManager} - ignored if {@code null}
         * @param userFactories The currently available {@link UserAuth} factories - if not
         * {@code null}/empty then they are used as-is.
         * @return The resolved {@link List} of {@link NamedFactory} for the {@link UserAuth}s
         */
        public static List<NamedFactory<UserAuth>> resolveUserAuthFactories(
                ServerAuthenticationManager manager, List<NamedFactory<UserAuth>> userFactories) {
            if (GenericUtils.size(userFactories) > 0) {
                return userFactories;   // use whatever the user decided
            }

            if (manager == null) {
                return Collections.emptyList();
            }

            List<NamedFactory<UserAuth>> factories = new ArrayList<>();
            if (manager.getPasswordAuthenticator() != null) {
                factories.add(DEFAULT_USER_AUTH_PASSWORD_FACTORY);
                factories.add(DEFAULT_USER_AUTH_KB_INTERACTIVE_FACTORY);
            } else if (manager.getKeyboardInteractiveAuthenticator() != null) {
                factories.add(DEFAULT_USER_AUTH_KB_INTERACTIVE_FACTORY);
            }

            if (manager.getPublickeyAuthenticator() != null) {
                factories.add(DEFAULT_USER_AUTH_PUBLIC_KEY_FACTORY);
            }

            if (manager.getGSSAuthenticator() != null) {
                factories.add(DEFAULT_USER_AUTH_GSS_FACTORY);
            }

            return factories;
        }
    }
}

/*
 * File: GetObjectHistoryFilter.java
 *
 * Copyright 2007 Macquarie E-Learning Centre Of Excellence
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.server.security.xacml.pep.rest.filters;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fcrepo.common.Constants;
import org.fcrepo.server.security.xacml.pep.PEPException;
import org.fcrepo.server.security.xacml.pep.ResourceAttributes;
import org.fcrepo.server.security.xacml.util.LogUtil;
import org.fcrepo.server.utilities.CXFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.ctx.RequestCtx;


/**
 * Handles the getObjectHistory operation.
 *
 * @author nishen@melcoe.mq.edu.au
 */
public class GetObjectHistoryFilter
        extends AbstractFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(GetObjectHistoryFilter.class);

    /**
     * Default constructor.
     *
     * @throws PEPException
     */
    public GetObjectHistoryFilter()
            throws PEPException {
        super();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.server.security.xacml.pep.rest.filters.RESTFilter#handleRequest(javax.servlet
     * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public RequestCtx handleRequest(HttpServletRequest request,
                                    HttpServletResponse response)
            throws IOException, ServletException {
        String[] parts = getPathParts(request);
        if (parts.length < 2) {
            logger.error("Not enough path components on the URI: {}", request.getRequestURI());
            throw new ServletException("Not enough path components on the URI: " + request.getRequestURI());
        }

        String pid = parts[1];

        RequestCtx req = null;

        Map<URI, AttributeValue> actions = new HashMap<URI, AttributeValue>();
        Map<URI, AttributeValue> resAttr;

        try {
            resAttr = ResourceAttributes.getResources(pid);

            actions.put(Constants.ACTION.ID.getURI(),
                        Constants.ACTION.GET_OBJECT_HISTORY
                                .getStringAttribute());
            actions.put(Constants.ACTION.API.getURI(),
                        Constants.ACTION.APIA.getStringAttribute());

            req =
                    getContextHandler().buildRequest(getSubjects(request),
                                                     actions,
                                                     resAttr,
                                                     getEnvironment(request));

            LogUtil.statLog(request.getRemoteUser(),
                            Constants.ACTION.GET_OBJECT_HISTORY.uri,
                            pid,
                            null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            CXFUtility.getFault(e);
        }

        return req;
    }
}

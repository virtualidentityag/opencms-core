/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.editors.usergenerated;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.util.CmsUUID;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

public class CmsFormSessionFactory {

    private ConcurrentHashMap<CmsUUID, CmsSessionQueue> m_queues = new ConcurrentHashMap<CmsUUID, CmsSessionQueue>();

    public CmsFormSession createSession(CmsObject cms, HttpServletRequest request, CmsFormConfiguration config)
    throws CmsException {

        CmsFormSession session = createSession(cms, config);
        request.getSession(true).setAttribute("FS", session);
        return session;
    }

    public CmsFormSession createSession(CmsObject cms, HttpServletRequest request, String sitePath) throws CmsException {

        CmsFormConfigurationReader reader = new CmsFormConfigurationReader(cms);
        CmsFile configFile = cms.readFile(sitePath);
        CmsFormConfiguration config;
        try {
            config = reader.readConfiguration(configFile);
            return createSession(cms, request, config);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private CmsFormSession createSession(CmsObject cms, CmsFormConfiguration config) throws CmsException {

        getQueue(config).waitForSlot();
        return new CmsFormSession(cms, config);
    }

    private CmsSessionQueue getQueue(CmsFormConfiguration config) {

        if (m_queues.get(config.getId()) == null) {
            m_queues.put(config.getId(), CmsSessionQueue.createQueue(config));
        }
        return m_queues.get(config.getId());
    }

}
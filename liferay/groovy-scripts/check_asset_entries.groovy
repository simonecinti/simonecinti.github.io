//-----------------------------------------
// Check asset entries - groovy script
//-----------------------------------------
// This groovy script will launch the asset entries checker,
// who will check for e-mail notifications to be sent.
//
// Tested with Liferay 7.1
// author: Simone Cinti
//------------------------------------------

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import com.liferay.portal.scripting.groovy.internal.GroovyExecutor;

Object _assetEntriesCheckerUtil = _getServiceImpl("com.liferay.asset.publisher.web.internal.messaging.AssetEntriesCheckerUtil");

out.println("start checking asset entries for notifications...");
_assetEntriesCheckerUtil.checkAssetEntries();


//------------------------------------------

def _getServiceImpl(clazzName) throws Exception {
       Bundle bnd = FrameworkUtil.getBundle(GroovyExecutor.class);
       ServiceTracker st = new ServiceTracker(bnd.getBundleContext(), clazzName, null);
       st.open();
       if (st.getService() == null) {
             throw new RuntimeException("service implementation for " + clazzName + " not found in registry");
       }
       return st.getService();
}


import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.scripting.groovy.internal.GroovyExecutor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

//-----------------------------------------
// OSGi ServiceTracker from Groovy Script
//-----------------------------------------
// 
// Use: _getServiceImpl(clazzName) to get service or components implementations from ServiceTracker.
// For instance, the script shows how to get a runtime UserLocalService implementation
//
// author: Simone Cinti
//------------------------------------------


UserLocalService _userLocalService = (UserLocalService) _getServiceImpl(UserLocalService.class.getName());
out.println(_userLocalService.class.getName());


def _getServiceImpl(clazzName) throws Exception {
	Bundle bnd = FrameworkUtil.getBundle(GroovyExecutor.class);
	ServiceTracker st = new ServiceTracker(bnd.getBundleContext(), clazzName, null);
	st.open();
	if (st.getService() == null) {
		throw new RuntimeException("service implementation for " + clazzName + " not found in registry");
	}
	return st.getService();
}
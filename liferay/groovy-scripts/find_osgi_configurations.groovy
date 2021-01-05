// -------------------------------------------------------------
// Groovy script - find Liferay OSGi Configurations
// -------------------------------------------------------------
//
// author: Simone Cinti - Jan. 2021
//
// NOTE: successfully tested on Liferay 7.1.3 CE GA4
// -------------------------------------------------------------

import com.liferay.portal.scripting.groovy.internal.GroovyExecutor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.Configuration;
import com.liferay.portal.kernel.util.StringBundler;
import java.util.Arrays;
import java.util.List;

String serviceFactoryPidToFind = "com.liferay.*";

ConfigurationAdmin _configurationAdmin = (ConfigurationAdmin) _getServiceImpl(ConfigurationAdmin.class.getName());

if (_configurationAdmin == null) {

	out.println("unable to get the ConfigurationAdmin: null reference.");
}
else {
	try {
		Configuration[] cfgs = _findExistingConfiguration(_configurationAdmin, serviceFactoryPidToFind);
		
		if (cfgs.length > 0) {
			for (Configuration cfg: cfgs) {
				out.println(cfg);
			}
		}
		else {
			out.println("No OSGI configuration found having serviceFactoryPid: " + serviceFactoryPid);
		}
	} catch (Exception ex) {
		ex.printStackTrace(out);
	}
}

def _getServiceImpl(clazzName) throws Exception {
	Bundle bnd = FrameworkUtil.getBundle(GroovyExecutor.class);
	ServiceTracker st = new ServiceTracker(bnd.getBundleContext(), clazzName, null);
	st.open();

	if (st.getService() == null) {
		throw new RuntimeException("service implementation for " + clazzName + " not found in registry");
	}

	return st.getService();
}

def _findExistingConfiguration(configurationAdmin, serviceFactoryPid) throws Exception {

	StringBundler sb = new StringBundler(5);

	sb.append("(");
	sb.append("service.factoryPid");
	sb.append("=");
	sb.append(serviceFactoryPid);
	sb.append(")");

	Configuration[] configurations = configurationAdmin.listConfigurations(
		sb.toString());

	if (configurations == null) {
	     configurations = new Configuration[0];
	}

	return configurations;
}
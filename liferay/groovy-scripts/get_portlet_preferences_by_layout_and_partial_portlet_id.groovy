import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.PortletKeys;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.portlet.PortletPreferences;

long plid = 36693L;
Layout layout = LayoutLocalServiceUtil.getLayout(plid);

_getPortletPreferencesByLayoutAndPartialPortletId(layout , "_portlet");

def _getPortletPreferencesByLayoutAndPartialPortletId(layout, partialPortletId) {
	JSONObject result = JSONFactoryUtil.createJSONObject();
	if (layout.getLayoutType() instanceof LayoutTypePortlet) {
		List<String> portletIds = ((LayoutTypePortlet)layout.getLayoutType()).getPortletIds();
		for (String portletId: portletIds) {
			if (portletId.contains(partialPortletId)) {
				if (!result.has(portletId)) {
					result.put(portletId, JSONFactoryUtil.createJSONArray());
				}
				JSONObject jo = _getPortletPreferencesJSONObject(layout, portletId);
				if (jo != null) {
					JSONArray ja = result.getJSONArray(portletId);
					ja.put(jo);
				}
			}
		}
	}
	return result;
}

def _getPortletPreferencesJSONObject(layout, portletId) {
	JSONObject result = null;
	try {
               out.println("getting preferences for portletId : " + portletId + " and layout plid: " + layout.getPlid());
		PortletPreferences preferences = PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);
		if (preferences != null && !preferences.getMap().isEmpty()) {
			out.println("getting preferences for layout: '" + layout.getFriendlyURL() + "' plid: " + layout.getPlid() + ", portletId: '" + portletId + "'");
			result = JSONFactoryUtil.createJSONObject();
			result.put("layout-uuid", layout.getUuid());
			result.put("layout-is-private", layout.getPrivateLayout());
			result.put("layout-friendly-url", layout.getFriendlyURL());
			result.put("layout-group", layout.getGroup().getName());
			result.put("portlet-preferences", JSONFactoryUtil.looseSerializeDeep(preferences.getMap()));
		}
	} catch (Exception ex) {
		out.println("exception : " + ex.getMessage());
	}
	return result;
}

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//----------------------------------------------------------
// Visits all child pages given parent plid - Groovy script  
//----------------------------------------------------------
//
// Usage: _visitChildLayouts(parentLayout, maxDepth) 
//        where: 
//              - parentLayout : is the parent Layout from which to start
//              - maxDepth     : is the maximum depth of tree to reach. -1 means no limits!       
//
   long parentPlid = 36693L;
   int maxDepth = -1L; //


// Tested with Liferay 7.0 CE / DXP
// author: Simone Cinti - 25/05/2020
//----------------------------------------------------------


Layout parentLayout = LayoutLocalServiceUtil.fetchLayout(parentPlid);
for(Layout l: _visitChildLayouts(parentLayout,  3)) {
	out.println("found child layout page with friendly URL: \"" + l.getFriendlyURL() + "\" having plid: " + l.getPlid());
}

def _visitChildLayouts(parentLayout, maxDepth) {
	Set<Layout> result = new LinkedHashSet<Layout>();
	Set<Long> parentLayoutIds = new LinkedHashSet<Long>();
	long parentLayoutId = parentLayout.getLayoutId();
	long groupId = parentLayout.getGroupId();
	parentLayoutIds.add(parentLayoutId);
	int depthCount = 0;
	while (!parentLayoutIds.isEmpty() && (maxDepth < 0 || depthCount < maxDepth)) {
		parentLayoutId = parentLayoutIds.iterator().next();
		parentLayoutIds.remove(parentLayoutId);
		try {
			for (boolean privateLayout: [true,false]) {
				if (LayoutLocalServiceUtil.hasLayouts(groupId, privateLayout, parentLayoutId)) {
					List<Layout> ll = LayoutLocalServiceUtil.getLayouts(groupId, privateLayout, parentLayoutId);
					for (Layout l : ll) {
						result.add(l);
						if (LayoutLocalServiceUtil.hasLayouts(groupId, l.isPrivateLayout(), l.getLayoutId())) {
							parentLayoutIds.add(l.getLayoutId());
						}
					}
					depthCount++;
				}
			}
		} catch (Exception ex) {
			out.println("Exception: " + ex.getMessage());
		}
	}
	out.println("total child layouts found:" + result.size() + " - max depth reached: " + depthCount);
	return result;
}

// -------------------------------------------------------------
// Groovy script - delete notified entry ids from asset publisher
// -------------------------------------------------------------
// This script could be useful when you want to remove some
// asset entry ids from the assetpublisher portlet preferences
// in order to re-send the e-mail notification, when the
// asset publiser entries checker will be rescheduled.  
//
//
// author: Simone Cinti - April 2021
//
// NOTE: successfully tested on Liferay 7.1.3 CE GA4
// -------------------------------------------------------------


import com.liferay.portal.kernel.portlet.*;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.*;
import java.io.*;
import java.util.*;
import javax.portlet.*;

long companyId = 20155L;				//here the companyId
long portletPreferencesId = 0L; 		//here the portletPreferencesId
long[] assetEntryIdsToDelete = [0L]; 	//here the entryIds of asset entries you want to delete

_deleteNotifiedAssetEntryIdsFromPortletPreferences(20155L , portletPreferencesId, assetEntryIdsToDelete);



// -------------------------------------------------------------------------

def _deleteNotifiedAssetEntryIdsFromPortletPreferences(companyId, portletPreferencesId, assetEntryIdsToDelete) {

com.liferay.portal.kernel.model.PortletPreferences portletPreferencesModel = 
	PortletPreferencesLocalServiceUtil.fetchPortletPreferences(portletPreferencesId);
	
	PortletPreferences portletPreferences =
		PortletPreferencesFactoryUtil.fromXML(
			companyId, portletPreferencesModel.getOwnerId(),
			portletPreferencesModel.getOwnerType(),
			portletPreferencesModel.getPlid(),
			portletPreferencesModel.getPortletId(),
			portletPreferencesModel.getPreferences());


	long[] notifiedAssetEntryIds = GetterUtil.getLongValues(
		portletPreferences.getValues("notifiedAssetEntryIds", null));

	out.println("loaded: " + notifiedAssetEntryIds.length + " notified asset entry ids in current portlet preferences...");

	List<Long> newAssetEntryIds = new ArrayList<Long>();
	for (long assetEntryId : notifiedAssetEntryIds) {
		newAssetEntryIds.add(assetEntryId);
	}
	
	for (int i = 0 ; i < assetEntryIdsToDelete.length; i++) {
		long assetEntryIdToDelete = assetEntryIdsToDelete[i];
		if (ArrayUtil.contains(
				notifiedAssetEntryIds, assetEntryIdToDelete)) {

			newAssetEntryIds.remove((Long)assetEntryIdToDelete);
			out.println("deleting assetEntryId:" + assetEntryIdToDelete);
		}
	}

	out.println("now we will have: " + newAssetEntryIds.size() + " asset entry ids to store in portlet preferences...");

	try {
		portletPreferences.setValues(
			"notifiedAssetEntryIds",
			StringUtil.split( ListUtil.toString(newAssetEntryIds, "") )
		);

		out.println("storing preferences for portletPreferencesId: " + portletPreferencesId);
		portletPreferences.store();
	}
	catch (Exception ex) {
	  ex.printStackTrace(out);
	}
}

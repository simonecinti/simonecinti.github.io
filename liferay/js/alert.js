/*
 * alert.js
 *
 * 
 * Utility functions to show Liferay Alert's using Liferay.Alert 
 *
 *
 * author: Simone Cinti
 *
 */

function showWarningAlert(containerElement, message, title) {
	showLiferayAlert(containerElement, true, 0, 0, 1000, "exclamation-full", message, title, "warning");
}
function showDangerAlert(containerElement, message, title) {
	showLiferayAlert(containerElement, true, 0, 0, 1000, "exclamation-full", message, title, "danger");
}
function showInfoAlert(containerElement, message, title) {
	showLiferayAlert(containerElement, true, 0, 0, 1000, "info-circle", message, title, "info");
}

function showLiferayAlert(containerElement, closeable, hideDelay, showDelay, durationDelay, iconClass, message, title, type) {
	new Liferay.Alert(
		{
			closeable: closeable,
			delay: {
				hide: hideDelay,
				show: showDelay
                        },
			duration: durationDelay,
			icon: iconClass,
			message: message,
			namespace: '<portlet:namespace />',
			title: title,
			type: type
		}
	).render(containerElement);
}

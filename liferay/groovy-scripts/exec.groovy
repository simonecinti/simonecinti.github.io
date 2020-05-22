import com.liferay.portal.kernel.scripting.ScriptingExecutor;
import com.liferay.portal.kernel.scripting.ScriptingUtil;
import com.liferay.portal.kernel.util.FileUtil;
import java.util.HashMap;
import java.util.Map;		

// ------------------------------------
// Groovy executor
// ------------------------------------
// 22/05/2020 - Liferay 7.0 CE / DXP
// ------------------------------------

String scriptFileName = "/tmp/the_script_file_name.groovy";

Map<String,Object> inParams = new HashMap<String, Object>();
inParams.put("out", out);

String scriptToExec = FileUtil.read(scriptFileName);
ScriptingExecutor se = ScriptingUtil.createScriptingExecutor("groovy", true);
ScriptingUtil.getScripting().exec(null, inParams, "groovy", scriptToExec);

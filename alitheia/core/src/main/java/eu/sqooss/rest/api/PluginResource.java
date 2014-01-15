package eu.sqooss.rest.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.wrappers.ResponseBuilder;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@Path("/api/plugin/")
public class PluginResource {
	
	private PluginAdmin pluginAdmin;
	
	public PluginResource() {
		pluginAdmin = AlitheiaCore.getInstance().getPluginAdmin();
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("info/list")
	public Collection<PluginInfo> listPluginsInfo() {
		return pluginAdmin.listPlugins();
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("info/{hashcode}")
	public PluginInfo getPluginInfo(@PathParam("hashcode") String pluginhashcode) {
		return pluginAdmin.getPluginInfo(pluginhashcode);
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Consumes({"application/xml", "application/json"})
	@Path("info")
	public PluginInfo getPluginInfo(AlitheiaPlugin p) {
		return pluginAdmin.getPluginInfo(p);
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Consumes({"application/xml", "application/json"})
	@Path("get")
	public AlitheiaPlugin getPluginInfo(PluginInfo pinfo) {
		return pluginAdmin.getPlugin(pinfo);
	}
	
	@PUT
	@Path("install/{hashcode}")
	public Response installPlugin(@PathParam("hashcode") String pluginhashcode) {
		if(pluginAdmin.installPlugin(pluginhashcode)) {
			PluginInfo pInfo = pluginAdmin.getPluginInfo(pluginhashcode);
			pluginAdmin.pluginUpdated(pluginAdmin.getPlugin(pInfo));
			return ResponseBuilder.simpleResponse("Plugin installation successful");
		}
		else
			return ResponseBuilder.internalServerErrorResponse("Plugin installation was unsuccessful");
	}
	
	@DELETE
	@Path("uninstall/{hashcode}")
	public Response uninstallPlugin(@PathParam("hashcode") String pluginhashcode) {
		if(pluginAdmin.uninstallPlugin(pluginhashcode)) {
			return ResponseBuilder.simpleResponse("A Job has beeen scheduled to remove the Plugin");
		}
		else
			return ResponseBuilder.internalServerErrorResponse("Plugin cannot be uninstalled");
	}
	
	@DELETE
	@Path("uninstall/{pluginid}")
	public Response uninstallPlugin(@PathParam("pluginid") Long pluginId) {
		if(pluginAdmin.uninstallPlugin(pluginId))
			return ResponseBuilder.simpleResponse("A Job has beeen scheduled to remove the Plugin");
		else
			return ResponseBuilder.internalServerErrorResponse("Plugin cannot be uninstalled");
	}
	
}

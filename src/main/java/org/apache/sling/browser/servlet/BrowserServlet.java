package org.apache.sling.browser.servlet;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.browser.resource.BrowserResource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
@Properties({ @Property(label = "Description", name = Constants.SERVICE_DESCRIPTION, value = "JCR Browser Servlet", propertyPrivate = true),
		@Property(label = "Vendor", name = Constants.SERVICE_VENDOR, value = "JCR Browser", propertyPrivate = true),
		@Property(name = "sling.servlet.paths", value = "/services/browser") })
public class BrowserServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final String ROOT_PATH = "/";
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private static final String[] SUPPORTED_EXTENSIONS = { 
		"jsp", "htm", "html", "java", "css", "txt", "xml", "js", "json", "jsonp", "esp", "groovy", "md" , "sh"
	};
	

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private MimeTypeService mineTypeService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {
		String path = request.getRequestPathInfo().getSuffix();
		BrowserResource browserResource = null;
		if (path == null) {
			/* root */
			path = ROOT_PATH;
		}
		if (path != null) {
			Resource r = request.getResourceResolver().resolve(path);
			if (!ResourceUtil.isNonExistingResource(r)) {
				browserResource = new BrowserResource(r);
			}
		}
		if (browserResource == null) {
			browserResource = new BrowserResource(request.getResourceResolver().resolve(ROOT_PATH));
		}
		boolean isTidy = false;
		boolean isModeChildren = false;
		for (String selector : request.getRequestPathInfo().getSelectors()) {
			if (selector.equals("tidy")) {
				isTidy = true;
			}
			if (selector.equals("children")) {
				isModeChildren = true;
			}
		}

		JSONWriter jsonWriter = new JSONWriter(response.getWriter());
		jsonWriter.setTidy(isTidy);
		try {
			if (isModeChildren) {
				listChildren(browserResource.getChildren().iterator(),jsonWriter, mineTypeService);
			} else {
				jsonWriter.array();
					jsonWriter.object().key("label").value(path.equals(ROOT_PATH) ? "jcr:root" : browserResource.getNode().getName());
						jsonWriter.key("path").value(browserResource.getPath());
						jsonWriter.key("id").value(browserResource.getPath());
						jsonWriter.key("uuid").value(browserResource.getIdentifier());
						jsonWriter.key("editable").value(browserResource.isModfiable());
						jsonWriter.key("nodeType").value(browserResource.getSimpleNodeType());
						if (browserResource.getParent() != null) {
							jsonWriter.key("parentId").value(browserResource.getParent().getPath());
						}
						if (browserResource.getSimpleNodeType().equals(JcrConstants.NT_FILE)) {
							jsonWriter.key("fileType").value(getSupportedFileType(browserResource.getNode()) );
							jsonWriter.key("extension").value(mineTypeService.getExtension(browserResource.getNode().getProperty("jcr:content/jcr:mimeType").getString().toLowerCase()));
						}
						jsonWriter.key("children");
						listChildren(browserResource.getChildren().iterator(),jsonWriter, mineTypeService);
					jsonWriter.endObject();
				jsonWriter.endArray();
			}
		} catch (JSONException je) {
			log.error(je.getMessage(),je.getCause());
		} catch (RepositoryException e) {
			log.error(e.getMessage(),e.getCause());
		}

	}

	private static void listChildren(Iterator<BrowserResource> it, JSONWriter jsonWriter, MimeTypeService mtService) throws JSONException, RepositoryException {
		if (it != null && it.hasNext()) {
			jsonWriter.array();
			while (it.hasNext()) {
				BrowserResource resource = it.next();
				jsonWriter.object().key("label").value(resource.getNode().getName());
				jsonWriter.key("path").value(resource.getPath());
				jsonWriter.key("id").value(resource.getPath());
				jsonWriter.key("uuid").value(resource.getIdentifier());
				jsonWriter.key("editable").value(resource.isModfiable());
				jsonWriter.key("nodeType").value(resource.getSimpleNodeType());
				if (resource.getParent() != null) {
					jsonWriter.key("parentId").value(resource.getParent().getPath());
				}
				if (resource.getSimpleNodeType().equals(JcrConstants.NT_FILE)) {
					jsonWriter.key("fileType").value(getSupportedFileType(resource.getNode()) );
					jsonWriter.key("extension").value(mtService.getExtension(resource.getNode().getProperty("jcr:content/jcr:mimeType").getString().toLowerCase()));				}
				if (resource.getChildren().iterator().hasNext()) {
					jsonWriter.key("load_on_demand").value(true);
				}
				jsonWriter.endObject();
			}
			jsonWriter.endArray();
		}
	}
	
	/* text editor can support these file types */
	private static String getSupportedFileType(Node node) throws RepositoryException {
		if (!node.isNodeType(JcrConstants.NT_FILE)) {
			return null;
		}
		int typeIndex = node.getName().lastIndexOf('.');
		if (typeIndex != -1) {
			String ext =node.getName().substring(typeIndex + 1);
			for (String extension : SUPPORTED_EXTENSIONS) {
				if (extension.equalsIgnoreCase(ext)) {
					return ext;
				}
			}
		} 
		return null;
	}
	
	
}
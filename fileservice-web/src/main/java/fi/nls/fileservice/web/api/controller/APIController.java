package fi.nls.fileservice.web.api.controller;

import fi.nls.fileservice.web.controller.BaseController;

//@Controller
//@RequestMapping("/api")
public class APIController extends BaseController {
    /*
     * @Inject @Named("datarepo") private Repository repository;
     * 
     * @Inject private Environment env;
     * 
     * @Inject @Named("aclpm") private PersistenceManager aclManager;
     * 
     * @Inject private FileService service;
     * 
     * @Inject private TokenGenerator tokenGenerator;
     * 
     * @RequestMapping(value="/files/**", method=RequestMethod.DELETE) public
     * 
     * @ResponseBody String deleteFile(HttpServletRequest request) { String path
     * = getPath(request); service.delete(path); return "OK"; }
     * 
     * @RequestMapping(value="/files/**", method=RequestMethod.POST) public
     * 
     * @ResponseBody String ajaxDeleteFile(@RequestParam(value="_method") String
     * method, HttpServletRequest request) { if ("delete".equals(method)) {
     * String path = getPath(request); service.delete(path); return "OK"; } //
     * TODO return "NOOP"; }
     * 
     * @RequestMapping(value="/files/**", method=RequestMethod.GET) public
     * ResponseEntity<Feed> directoryListing(HttpServletRequest request,
     * HttpServletResponse response, UriComponentsBuilder uriComponentsBuilder)
     * throws PathNotFoundException, RepositoryException, IOException {
     * 
     * uriComponentsBuilder.path("/api/files");
     * 
     * String path = getPath(request); Session session = repository.login(
     * AuthorizationContextHolder.getCredentials() ); Node node =
     * session.getNode(path); if (NodeUtils.isFolder(node)) { DetachedNode
     * detachedNode = new DetachedNode(node,true); session.logout();
     * DirectoryFeedBuilder builder = new DirectoryFeedBuilder(detachedNode,
     * uriComponentsBuilder);
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * responseHeaders.add("Content-Type",
     * "application/atom+xml; charset=utf-8"); return new
     * ResponseEntity<Feed>(builder.getFeed(),responseHeaders, HttpStatus.OK);
     * 
     * } else { super.serveFile(node, request, response,true); return null; } }
     * 
     * @RequestMapping(value="/files/**", method=RequestMethod.PUT) public
     * ResponseEntity<String> uploadFile(HttpServletRequest request, InputStream
     * in) {
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * 
     * String path = getPath(request); String[] pathComponents =
     * Formatter.getPathComponents(path); // path, filename
     * 
     * if (pathComponents.length == 2) { service.saveFile(pathComponents[0],
     * pathComponents[1], in); responseHeaders.add("Content-Type",
     * "text/plain; charset=utf-8"); return new ResponseEntity<String>("OK",
     * responseHeaders, HttpStatus.OK); }
     * 
     * responseHeaders.add("Content-Type", "text/plain; charset=utf-8"); return
     * new ResponseEntity<String>("Bad location: " + path, responseHeaders,
     * HttpStatus.BAD_REQUEST);
     * 
     * }
     * 
     * @RequestMapping(value="/upload",method=RequestMethod.GET,
     * produces="application/json") public @ResponseBody JqueryFileUploadUri
     * doBlueImpInfo(UriComponentsBuilder builder) { JqueryFileUploadUri uri =
     * new JqueryFileUploadUri(builder.build().toUriString()); return uri; }
     * 
     * @RequestMapping(value="/upload",method=RequestMethod.POST,
     * produces="application/json") public ResponseEntity<List<FileMeta>>
     * doMimeMultiPartUpload(HttpServletRequest request, UriComponentsBuilder
     * uriComponentsBuilder) throws FileUploadException, IOException {
     * 
     * List<FileMeta> metas = new ArrayList<FileMeta>(1);
     * 
     * // not using Spring MVC's CommonMultiPartResolver, cos it doesn't stream
     * the uploaded file.. if ( ServletFileUpload.isMultipartContent(request) )
     * { ServletFileUpload upload = new ServletFileUpload();
     * 
     * String resultFilePath = null; String parentPath = null;
     * 
     * FileItemIterator iter = upload.getItemIterator(request); while
     * (iter.hasNext()) { FileItemStream item = iter.next(); if
     * (item.isFormField()) { if ("path".equals(item.getFieldName())) {
     * parentPath = Streams.asString(item.openStream()); } } else { String
     * filename = item.getName(); InputStream in = item.openStream();
     * resultFilePath = service.saveFile(parentPath, filename, in);
     * 
     * DetachedNode node = service.getNode(resultFilePath);
     * 
     * FileMeta meta = new FileMeta(node.getName(), node.getLength(),
     * "/modeshape/api/delete/" + node.getPath()); //TODO remove HC
     * metas.add(meta);
     * 
     * } }
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * responseHeaders.add("Location", uriComponentsBuilder.path("/api/files/" +
     * resultFilePath).build().toUriString()); return new
     * ResponseEntity<List<FileMeta>>(metas, responseHeaders,
     * HttpStatus.CREATED); } else { return new
     * ResponseEntity<List<FileMeta>>(metas, HttpStatus.BAD_REQUEST); }
     * 
     * }
     * 
     * @RequestMapping(value="/createshare",method=RequestMethod.POST,
     * produces="application/json") public ResponseEntity<JqueryFileUploadUri>
     * createShare(HttpServletRequest request, UriComponentsBuilder
     * uriComponentsBuilder) {
     * 
     * String parentPath = env.getProperty("share.parent.folder"); String token
     * = tokenGenerator.generateToken();
     * 
     * String createdFolderAbsPath = service.createShareFolder(parentPath,
     * token, token, env.getProperty("share.validity.days", Integer.class));
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * //responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
     * responseHeaders.add("Location", uriComponentsBuilder.path("/api/files" +
     * createdFolderAbsPath).build().toUriString());
     * 
     * UriComponentsBuilder ucb =
     * UriComponentsBuilder.fromHttpUrl(env.getProperty("external.url.root"));
     * ucb.pathSegment("token", token);
     * 
     * responseHeaders.add("Link", "<" + ucb.build().toUriString()
     * +">; rel=alternate");
     * 
     * JqueryFileUploadUri uri = new JqueryFileUploadUri(createdFolderAbsPath);
     * 
     * return new ResponseEntity<JqueryFileUploadUri>(uri,responseHeaders,
     * HttpStatus.CREATED); }
     * 
     * @RequestMapping(value="/sharepaths",method=RequestMethod.POST) public
     * ResponseEntity<String> createShareToPath(@RequestParam(value="path")
     * String[] paths, HttpServletRequest request, UriComponentsBuilder
     * uriComponentsBuilder) throws IOException {
     * 
     * String token = tokenGenerator.generateToken();
     * 
     * AclEntryImpl acl = new AclEntryImpl(); acl.setPrincipal(new
     * NLSPrincipal(token)); for (String path : paths) { PermissionImpl
     * permission = new PermissionImpl(path, new String[] { "read" });
     * acl.addPermission(permission); }
     * 
     * aclManager.storeAcl(acl);
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
     * //responseHeaders.add("Location", uriComponentsBuilder.path("/api/files"
     * + createdFolderAbsPath).build().toUriString());
     * 
     * UriComponentsBuilder ucb =
     * UriComponentsBuilder.fromHttpUrl(env.getProperty("external.url.root"));
     * ucb.path(paths[0]); ucb.queryParam("token", token);
     * 
     * // ucb.pathSegment("token", token);
     * 
     * responseHeaders.add("Link", "<" + ucb.build().toUriString()
     * +">; rel=alternate");
     * 
     * return new ResponseEntity<String>("OK",responseHeaders,
     * HttpStatus.CREATED);
     * 
     * }
     */

}

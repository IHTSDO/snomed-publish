package com.ihtsdo.snomed.browse.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.browse.model.SparqlResults;
import com.ihtsdo.snomed.browse.model.SparqlResults.Binding;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Statement;

@Named
public class SparqlService {
    
    private static final Logger LOG = LoggerFactory.getLogger( SparqlService.class );
    
    @Value("${sparql.server.url}")
    private URL sparqlServerUrl;
    
    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;
    
    @Inject
    protected @Named("sparqlTemplate") RestTemplate restTemplate;
    
    @Inject
    protected HttpClient httpClient;
    
//    protected MultiValueMap<String, String> map;
//    protected HttpEntity<String> request;
//    protected HttpHeaders headers;
//    
    public static class AcceptHeaderHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        private long contentLength;
        
        public AcceptHeaderHttpRequestInterceptor(long contentLength){
            this.contentLength = contentLength;
        }
        
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException 
        {
            //System.out.println("In the interceptor!");
            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
            requestWrapper.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            requestWrapper.getHeaders().setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
            requestWrapper.getHeaders().setContentLength(contentLength);
            return execution.execute(requestWrapper, body);
        }

    }
    
    @PostConstruct
    public void init(){
        //headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_XML);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        //request = new HttpEntity<String>(headers);
        
        //map = new LinkedMultiValueMap<String, String>();
        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        
    }
    
    private XPathExpression countBindingsXpe;
    private XPathExpression allVariablesXpe;
    private XPathExpression variableNameXpe;
    private XPathExpression resultListXpe;
    private XPathExpression bindingXpe;
    private XPathExpression bindingValueClassXpe;
    private XPathExpression bindingValueLiteralXpe;
    private XPathExpression bindingValueLiteralDatatypeXpe;
    private XPathExpression bindingVariableXpe;
    //private XPathExpression resultBindingXpe;
    private XPath xpath;
    
    public SparqlService() throws XPathExpressionException{
        SparqlNamespaceContext nsContext = new SparqlNamespaceContext();
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(nsContext);  
        countBindingsXpe = xpath.compile("count(/sp:sparql/sp:results/sp:result)");
        allVariablesXpe = xpath.compile("/sp:sparql/sp:head/sp:variable");
        variableNameXpe = xpath.compile("@name");
        resultListXpe   = xpath.compile("/sp:sparql/sp:results/sp:result");
        bindingXpe = xpath.compile("sp:binding");
        bindingValueClassXpe = xpath.compile("sp:uri");
        bindingValueLiteralXpe = xpath.compile("sp:literal");
        bindingValueLiteralDatatypeXpe = xpath.compile("sp:literal/@datatype");
        bindingVariableXpe = xpath.compile("@name");
    }
    
    public SparqlResults runQuery(String query, long ontologyId) throws RestClientException, URISyntaxException, XPathExpressionException, ParserConfigurationException, SAXException, IOException{
        LOG.info("Running query [\n{}\n] on ontology {}", query, ontologyId);
        SparqlResults results = new SparqlResults();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("query", query);
        //headers.setContentLength(query.length());
        restTemplate.setInterceptors(Arrays.asList((ClientHttpRequestInterceptor)
                new AcceptHeaderHttpRequestInterceptor(query.getBytes().length)));
        Document dom = createDom(restTemplate.postForObject(
                sparqlServerUrl.toURI(), map, String.class));
        
        LOG.info("{} result bindings returned", countBindingsXpe.evaluate(dom, XPathConstants.NUMBER));
        
        NodeList variableNodes = (NodeList) allVariablesXpe.evaluate(dom, XPathConstants.NODESET);        
        for (int i = 0; i < variableNodes.getLength(); i++){
            results.getVariables().add((String)variableNameXpe.evaluate(variableNodes.item(i), XPathConstants.STRING));
        } 
        
        LOG.debug("Variables are [" + results.getVariables().toString() + "]");

        NodeList resultNodes = (NodeList) resultListXpe.evaluate(dom, XPathConstants.NODESET);
        for (int i = 0; i < resultNodes.getLength(); i++){
            Map<String, Binding> resultMap = new HashMap<>();
            NodeList bindings = (NodeList) bindingXpe.evaluate(resultNodes.item(i), XPathConstants.NODESET);
            for (int j = 0; j < bindings.getLength(); j++){
                
                String bindingVariable = (String)bindingVariableXpe.evaluate(bindings.item(j), XPathConstants.STRING);
                String bindingValue = (String)bindingValueClassXpe.evaluate(bindings.item(j), XPathConstants.STRING);
                Binding binding;
                
                if ((bindingValue == null) || (bindingValue.isEmpty())){
                    bindingValue = (String)bindingValueLiteralXpe.evaluate(bindings.item(j), XPathConstants.STRING);
                    String datatype = (String)bindingValueLiteralDatatypeXpe.evaluate(bindings.item(j), XPathConstants.STRING);
                    binding = new Binding(new URL(datatype), bindingValue);
                }else{
                    URL url = new URL(bindingValue);
                    binding = new Binding(url, getObjectForUrl(url, ontologyId)); 
                }
                resultMap.put(bindingVariable, binding);
                

            }
            results.getResults().add(resultMap);
        }

        return results;
    }
    
    private Object getObjectForUrl(URL url, long ontologyId){
        Stopwatch overAllstopwatch = new Stopwatch().start();
        try {
            String urlString = url.toString();
            if (urlString.contains("concept")){
                long serialisedId = getSerialisedId(urlString);
                LOG.debug("Found concept {} in sparql response with serialisedId {}", url, serialisedId);
                TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.serialisedId=:serialisedId  and c.ontology.id=:ontologyId", Concept.class)
                        .setParameter("serialisedId", serialisedId)
                        .setParameter("ontologyId", ontologyId);
                try {
                    return query.getSingleResult();
                } catch (NoResultException e) {
                    throw new UnexpectedSparqlResultException("Unable to find concept with serialisedId [" + serialisedId +"] in ontology [" + ontologyId + "]", e);
                }
            } else if (urlString.contains("statement")){
                long serialisedId = getSerialisedId(urlString);
                LOG.debug("Found statement {} in sparql response with serialisedId {}", url, serialisedId);
                TypedQuery<Statement> query = em.createQuery("SELECT s FROM Statement s where s.serialisedId=:serialisedId and s.ontology.id=:ontologyId", Statement.class)
                        .setParameter("serialisedId", serialisedId)
                        .setParameter("ontologyId", ontologyId);
                try {
                    return query.getSingleResult();
                } catch (NoResultException e) {
                    throw new UnexpectedSparqlResultException("Unable to find statement with serialisedId [" + serialisedId +"] in ontology [" + ontologyId + "]", e);
                }
            }else if (urlString.contains("description")){
                long serialisedId = getSerialisedId(urlString);
                LOG.debug("Found description {} in sparql response with serialisedId {}", url, serialisedId);
                TypedQuery<Description> query = em.createQuery("SELECT d FROM Description d where d.serialisedId=:serialisedId and d.ontology.id=:ontologyId", Description.class)
                        .setParameter("serialisedId", serialisedId)
                        .setParameter("ontologyId", ontologyId);
                try{
                    return query.getSingleResult();
                } catch (NoResultException e) {
                    throw new UnexpectedSparqlResultException("Unable to find description with serialisedId [" + serialisedId +"] in ontology [" + ontologyId + "]", e);
                }

            }else{
                return url;
            }
        } finally{
            overAllstopwatch.stop();
            LOG.debug("Object retrieved from db in " + overAllstopwatch.elapsed(TimeUnit.MILLISECONDS) + " miliseconds");
        }
    }

    protected long getSerialisedId(String urlString) {
        return Long.parseLong(urlString.substring(urlString.lastIndexOf('/') + 1));
    }
    
    private Document createDom(String is) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(is.getBytes()));
        
//        OutputFormat format = new OutputFormat(doc);
//        format.setIndenting(true);
//        XMLSerializer serializer = new XMLSerializer(System.out, format);
//        System.out.println("Document is REALLY: ");
//        serializer.serialize(doc);
//        Source source;
        
        return doc;
    }
    
    private class SparqlNamespaceContext implements NamespaceContext{
        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix == null){
                throw new NullPointerException("Null prefix");
            }
            else if (prefix == XMLConstants.DEFAULT_NS_PREFIX){
                return "http://www.w3.org/2005/sparql-results#";
            }
            else if ("sp".equals(prefix)){
                return "http://www.w3.org/2005/sparql-results#";
            }
            return XMLConstants.NULL_NS_URI;
        }

        // This method isn't necessary for XPath processing.
        @Override
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        // This method isn't necessary for XPath processing either.
        @Override
        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }

}

<%@page session="false" import="
    com.adobe.granite.ui.components.ds.DataSource,
    com.adobe.granite.ui.components.ds.EmptyDataSource,
    com.adobe.granite.ui.components.ds.SimpleDataSource,
    com.adobe.granite.ui.components.ds.ValueMapResource,
    org.apache.sling.api.resource.Resource,
    org.apache.sling.api.resource.ResourceMetadata,
    org.apache.sling.api.resource.ResourceResolver,
    org.apache.sling.api.resource.ValueMap,
    org.apache.sling.api.wrappers.ValueMapDecorator,
    java.util.ArrayList,
    java.util.HashMap,
    java.util.Iterator,
    java.util.List"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%
    final String TEXT_PROPERTY = "text";
    final String VALUE_PROPERTY = "value";
    final String EMPTY_STRING = "";

    request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
    final ResourceResolver resolver = resourceResolver;
    Resource datasource = resource.getChild("datasource");
    ValueMap dsProperties = datasource.getValueMap();

    //Datasource parameters
    String path = dsProperties.get("path", String.class);
    if (path != null && !path.isEmpty()) {
        Resource optionsResource = resolver.getResource(path);
        String defaultValueSelected = dsProperties.get("defaultValueSelected", String.class);
        String valueToIgnore = dsProperties.get("valueToIgnore", String.class);

        Iterator<Resource> resources = optionsResource.listChildren();
        List<Resource> finalOptions;
        Resource item;
        ValueMap valueMap;

        if (resources.hasNext()) {
            finalOptions = new ArrayList<>();
            while (resources.hasNext()) {
                item = resources.next();
                valueMap = new ValueMapDecorator(new HashMap<>());
                String text = item.getValueMap().get(TEXT_PROPERTY, EMPTY_STRING);
                String value = item.getValueMap().get(VALUE_PROPERTY, EMPTY_STRING);
                if (!value.isEmpty() && !text.isEmpty()) {
                    valueMap.put(VALUE_PROPERTY, value);
                    valueMap.put(TEXT_PROPERTY, text);

                    //if a default value is marked as selected
                    if(defaultValueSelected != null && value.toLowerCase().equals(defaultValueSelected.toLowerCase())) {
                        valueMap.put("selected", true);
                    }
                    if(!(valueToIgnore != null && value.toLowerCase().equals(valueToIgnore.toLowerCase()))) {
                        finalOptions.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", valueMap));
                    }
                }
            }

            DataSource ds = new SimpleDataSource(finalOptions.iterator());
            request.setAttribute(DataSource.class.getName(), ds);
        }
    }
%>

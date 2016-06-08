package com.suushiemaniac.gui.javafx.dynamic;

import com.suushiemaniac.lang.json.JSON;
import com.suushiemaniac.lang.json.util.StringUtils;
import com.suushiemaniac.lang.json.value.JSONType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class NodeParser {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private FXMLLoader loader;

    public NodeParser() {
        this.loader = new FXMLLoader();
    }

    /**
     * {
     *     "xmlImports" : [listWithAllPreFormattedXMLImports],
     *     "element" : "TheELementXMLAsJSONRep",
     *     "children" : [optionalChildren]
     * }
     *
     * @param jsonNode The node containing all details about the FXML element to be loaded
     * @return The JavaFX GUI scene graph element in the closest requested type possible
     * @throws IOException When the FXML parser is on strike
     */
    public <T extends Node> T parseFromJSON(JSONType jsonNode) throws IOException {
        Collection<String> xmlImports = jsonNode.get("xmlImports").collect().stream().map(s -> StringUtils.jsonUnwrap(s.toString())).collect(Collectors.toList());
        T element = this.parse(String.join("\n", HEADER, String.join("\n", xmlImports), jsonNode.get("element").toXMLString()));

        if (element instanceof Pane) {
            JSONType children = jsonNode.getOrDefault("children", JSON.fromJSONString("[]"));
            
            for (JSONType child : children) {
                ((Pane) element).getChildren().add(parseFromJSON(child));
            }
        }

        return element;
    }

    public <T extends Node> T parse(String fxmlString) throws IOException {
        return this.loader.load(new ByteArrayInputStream(fxmlString.getBytes()));
    }
}

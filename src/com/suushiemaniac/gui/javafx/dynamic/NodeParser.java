package com.suushiemaniac.gui.javafx.dynamic;

import com.suushiemaniac.lang.json.JSON;
import com.suushiemaniac.lang.json.value.JSONType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeParser {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private FXMLLoader loader;

    public NodeParser() {
        this.loader = new FXMLLoader();
    }

    /**
     * {
     *     "element" : "TheElementXMLAsJSONRep",
     *     "children" : [optionalChildren]
     * }
     *
     * @param jsonNode The node containing all details about the FXML element to be loaded
     * @return The JavaFX GUI scene graph element in the closest requested type possible
     * @throws IOException When the FXML parser is on strike
     */
    public <T extends Node> T parseFromJSON(JSONType jsonNode) throws IOException {
        List<String> xmlImports = new ArrayList<>();

        JSONType jsonElement = jsonNode.get("element");
        for (JSONType elementType : jsonElement) {
            for (Package p : Package.getPackages()) {
                String pName = p.getName();

                if (pName.startsWith("javafx")) {
                    String elementName = jsonElement.keyIndexOf(elementType).stringValue();
                    String cName = pName + "." + elementName;

                    if (isClass(cName)) {
                        xmlImports.add("<?import " + cName + "?>");
                    }
                }
            }
        }

        T element = this.parse(String.join("\n", HEADER, String.join("\n", xmlImports), jsonElement.toXMLString()));

        if (element instanceof Pane) {
            JSONType children = jsonNode.getOrDefault("children", JSON.fromJSONString("[]"));
            
            for (JSONType child : children) {
                ((Pane) element).getChildren().add(parseFromJSON(child));
            }
        }

        return element;
    }

    public <T extends Node> T parse(String fxmlString) throws IOException {
        for (Package p : Package.getPackages()) if (p.getName().startsWith("javafx")) System.out.println(p.getName());
        return this.loader.load(new ByteArrayInputStream(fxmlString.getBytes()));
    }
}

package com.suushiemaniac.gui.javafx.dynamic;

import com.suushiemaniac.lang.json.value.JSONType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

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

        return this.parse(String.join("\n", HEADER, String.join("\n", xmlImports), jsonElement.toXMLString()));
    }

    public <T extends Node> T parse(String fxmlString) throws IOException {
        return this.loader.load(new ByteArrayInputStream(fxmlString.getBytes()));
    }
}

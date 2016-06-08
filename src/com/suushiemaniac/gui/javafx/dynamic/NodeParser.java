package com.suushiemaniac.gui.javafx.dynamic;

import com.suushiemaniac.lang.json.JSON;
import com.suushiemaniac.lang.json.value.JSONType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NodeParser {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static Set<String> gatherImports(JSONType jsonNode) {
        Set<String> allImports = new HashSet<>();
        allImports.add(jsonNode.get("Type").stringValue());

        JSONType children = jsonNode.get("Children");

        if (children != null) {
            for (JSONType child : children) {
                allImports.addAll(gatherImports(child));
            }
        }

        return allImports;
    }

    private FXMLLoader loader;

    public NodeParser() {
        this.loader = new FXMLLoader();
    }

    public <T extends Node> T parseFromJSON(JSON jsonNode) throws IOException {
        Set<String> importSet = gatherImports(jsonNode);
        return this.parse(String.join("\n", HEADER, String.join("\n", importSet), jsonNode.toXMLString()));
    }

    public <T extends Node> T parse(String fxmlString) throws IOException {
        return this.loader.load(new ByteArrayInputStream(fxmlString.getBytes()));
    }
}

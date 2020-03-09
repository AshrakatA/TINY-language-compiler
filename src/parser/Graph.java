/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.IOException;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.*;

public class Graph {

    static SingleGraph graph = new SingleGraph("Abstract Syntax Tree");

    public static void main(String args[]) throws IOException {

        Parser.parser p = new Parser.parser();
        p.fill();
        graph.setStrict(false);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        for (int i = 0; i < p.nodes.size(); i++) {
            String NodeID = "";
            String temp = p.nodes.get(i).value;
            NodeID += p.nodes.get(i).value + i;
            p.nodes.get(i).value = NodeID;
            graph.addNode(NodeID);
            Node n = graph.getNode(NodeID);
            n.addAttribute("ui.label", temp);
            if (p.nodes.get(i).type.equals("stmt")) {
                n.addAttribute("ui.style", "shape: box;"
                        + "size: 100px, 50px;"
                        + "fill-mode: plain;"
                        + "fill-color: white;"
                        + "stroke-mode:plain;"
                        + " stroke-color:black;");
            } else {
                n.addAttribute("ui.style", "shape: circle;"
                        + "size: 60px, 40px;"
                        + "fill-mode: plain;"
                        + "fill-color: white;"
                        + "stroke-mode:plain;"
                        + "stroke-color:red;");
            }
            n.addAttribute("layout.frozen");
            n.addAttribute("x", p.nodes.get(i).x);
            n.addAttribute("y", p.nodes.get(i).y);
        }

        for (int i = 0; i < p.nodes.size(); i++) {
            for (int j = 0; j < p.nodes.get(i).Sibling.size(); j++) {
                graph.addEdge(i + "," + j, p.nodes.get(i).value, p.nodes.get(i).Sibling.get(j).value);
            }
        }

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
    }
}

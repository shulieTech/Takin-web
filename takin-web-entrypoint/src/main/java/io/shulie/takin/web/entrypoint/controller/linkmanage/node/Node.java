package io.shulie.takin.web.entrypoint.controller.linkmanage.node;

/**
 * @author  vernon
 * @date 2019/12/26 18:45
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {

    public String id;

    public String pid;

    public String content;

    List<Node> nodes = null;

    public Node() {
        this.nodes = new ArrayList<>();
    }

    public Node(String id, String pid, String content) {
        this.id = id;
        this.pid = pid;
        this.content = content;
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
    }

    public Node(Map map) {
        System.out.println(map);
        id = (String)map.get("id");
        pid = (String)map.get("pid");
        content = (String)map.get("name");
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNode(Node node) {
        this.nodes.add(node);
    }

    public List<Node> getNodes() {
        return this.nodes;
    }
}

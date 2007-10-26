package eu.sqooss.webui;

import java.util.Vector;

class ListView {
    
    Vector<String> items = new Vector();

    public ListView () {
        items.addElement(new String("Item 1"));
        items.addElement(new String("Item 2"));
        items.addElement(new String("Item 3"));
    }
    
    public void setItems (Vector<String> _items) {
        items = _items;
    }
    
    public Vector<String> getItems () {
        return items;
    }

    public String getHtml() {
        String html = "<!-- ListView -->\n<ul>";
        for (String item: items) {
            html = html.concat(new String("\n  <li>" + item + "</li>"));
        }
        html = html.concat("\n</ul>\n");
        return html;
    }
}
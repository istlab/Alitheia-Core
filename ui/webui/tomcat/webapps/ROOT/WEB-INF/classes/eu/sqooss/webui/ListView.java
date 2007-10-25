package eu.sqooss.webui;


class ListView {
    
    String[] items = new String[3];

    public ListView () {
        items[0] = "Item 1";
        items[1] = "Item 2";
        items[2] = "Item 3";
    }
    
    public void setItems (String[] _items) {
        items = _items;
    }
    
    public String[] getItems () {
        return items;
    }

    public String getHtml() {
        String html = "<!-- ListView -->\n<ul>";
        for (int i = 0; i < items.length; i++) {
            html = html.concat(new String("\n  <li>" + items[i] + "</li>"));
        }
        html = html.concat("\n</ul>\n");
        return html;
    }
}
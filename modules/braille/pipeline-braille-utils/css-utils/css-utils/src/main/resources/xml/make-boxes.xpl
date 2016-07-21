<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:css="http://www.daisy.org/ns/pipeline/braille-css"
                type="css:make-boxes"
                exclude-inline-prefixes="#all"
                version="1.0">
    
    <p:documentation>
        Generate boxes from elements.
    </p:documentation>
    
    <p:input port="source">
        <p:documentation>
            The 'display' properties of elements in the input must be declared in css:display
            attributes, and must conform to
            http://snaekobbi.github.io/braille-css-spec/#the-display-property. Elements with a
            'display' property of 'table' must have been previously marked with a css:table
            attribute. Their table cells must be marked with a css:table-cell attribute and table
            captions with a css:table-caption attribute.
        </p:documentation>
    </p:input>
    
    <p:output port="result">
        <p:documentation>
            Each element in the input generates zero or more boxes, represented by css:box elements
            in the output. A type attribute indicates the type of box: 'inline', 'block', 'table' or
            'table-cell'. Elements with a 'display' property of 'inline', 'block', 'list-item' or
            'table', as well as elements marked as css:table-cell or css:table-caption, generate a
            principal box which inherits any style and css:* attributes from the element and which
            becomes the container of child text nodes and boxes generated by the element's
            children. In the case of 'display: inline', the principal box has type 'inline'. In the
            case of css:table-cell, the principal box has type 'table-cell'. In the other cases the
            principal box has the type 'block'. Elements with 'display: none' don't generate boxes,
            and neither do any of their descendants. They result in css:_ elements that inherit
            style and css:* attributes from the element and become the parent of css:_ elements
            generated by their children. Elements with 'display: list-item' generate an additional
            inline marker box inside the principal box. If such an element's 'list-style-type'
            property is not 'none', the marker box contains a css:counter element with
            name="list-item" and a style attribute with the value of the 'list-style-type'
            property. Otherwise, the marker box is empty. Elements with 'display: table' generate an
            additional box of type 'table' inside the principal box. Of all the elements contained
            in an element with 'display: table', only the table caption and table cells generated
            boxes. All the other elements generate css:_ elements. The box that is generated by the
            table caption becomes a direct child of the principal box. The table box contains
            everything else. Existing css:_ elements in the input are retained in the output.
        </p:documentation>
    </p:output>
    
    <p:xslt>
        <p:input port="stylesheet">
            <p:document href="make-boxes.xsl"/>
        </p:input>
        <p:input port="parameters">
            <p:empty/>
        </p:input>
    </p:xslt>
    
</p:declare-step>

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-03/31/2009 04:14 PM(snajper)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.19 at 10:15:07 AM CEST 
//


package org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs21.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1}BooleanClause"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1}RationalClause"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1}StringClause"/>
 *       &lt;/choice>
 *       &lt;attribute name="leftArgument" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "booleanClause",
    "rationalClause",
    "stringClause"
})
@XmlRootElement(name = "SimpleClause")
public class SimpleClause {

    @XmlElement(name = "BooleanClause")
    protected BooleanClause booleanClause;
    @XmlElement(name = "RationalClause")
    protected RationalClause rationalClause;
    @XmlElement(name = "StringClause")
    protected StringClause stringClause;
    @XmlAttribute(required = true)
    protected String leftArgument;

    /**
     * Gets the value of the booleanClause property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanClause }
     *     
     */
    public BooleanClause getBooleanClause() {
        return booleanClause;
    }

    /**
     * Sets the value of the booleanClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanClause }
     *     
     */
    public void setBooleanClause(BooleanClause value) {
        this.booleanClause = value;
    }

    /**
     * Gets the value of the rationalClause property.
     * 
     * @return
     *     possible object is
     *     {@link RationalClause }
     *     
     */
    public RationalClause getRationalClause() {
        return rationalClause;
    }

    /**
     * Sets the value of the rationalClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link RationalClause }
     *     
     */
    public void setRationalClause(RationalClause value) {
        this.rationalClause = value;
    }

    /**
     * Gets the value of the stringClause property.
     * 
     * @return
     *     possible object is
     *     {@link StringClause }
     *     
     */
    public StringClause getStringClause() {
        return stringClause;
    }

    /**
     * Sets the value of the stringClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringClause }
     *     
     */
    public void setStringClause(StringClause value) {
        this.stringClause = value;
    }

    /**
     * Gets the value of the leftArgument property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftArgument() {
        return leftArgument;
    }

    /**
     * Sets the value of the leftArgument property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftArgument(String value) {
        this.leftArgument = value;
    }

}

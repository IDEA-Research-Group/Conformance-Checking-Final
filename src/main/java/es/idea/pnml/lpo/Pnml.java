//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.11.24 a las 09:51:49 AM CET 
//


package es.idea.pnml.lpo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lpo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="event" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="graphics">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="offset">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                         &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="graphics">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="position">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="lpoArc" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="graphics">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="userDrawn" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "lpo"
})
@XmlRootElement(name = "pnml")
public class Pnml {

    @XmlElement(required = true)
    protected Pnml.Lpo lpo;

    /**
     * Obtiene el valor de la propiedad lpo.
     * 
     * @return
     *     possible object is
     *     {@link Pnml.Lpo }
     *     
     */
    public Pnml.Lpo getLpo() {
        return lpo;
    }

    /**
     * Define el valor de la propiedad lpo.
     * 
     * @param value
     *     allowed object is
     *     {@link Pnml.Lpo }
     *     
     */
    public void setLpo(Pnml.Lpo value) {
        this.lpo = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="event" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="graphics">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="offset">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                               &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="graphics">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="position">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="lpoArc" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="graphics">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="userDrawn" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "event",
        "lpoArc"
    })
    public static class Lpo {

        @XmlElement(required = true)
        protected Pnml.Lpo.Name name;
        @XmlElement(required = true)
        protected List<Pnml.Lpo.Event> event;
        @XmlElement(required = true)
        protected List<Pnml.Lpo.LpoArc> lpoArc;
        @XmlAttribute(name = "id")
        protected String id;
        @XmlAttribute(name = "type")
        protected String type;

        /**
         * Obtiene el valor de la propiedad name.
         * 
         * @return
         *     possible object is
         *     {@link Pnml.Lpo.Name }
         *     
         */
        public Pnml.Lpo.Name getName() {
            return name;
        }

        /**
         * Define el valor de la propiedad name.
         * 
         * @param value
         *     allowed object is
         *     {@link Pnml.Lpo.Name }
         *     
         */
        public void setName(Pnml.Lpo.Name value) {
            this.name = value;
        }

        /**
         * Gets the value of the event property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the event property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEvent().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Pnml.Lpo.Event }
         * 
         * 
         */
        public List<Pnml.Lpo.Event> getEvent() {
            if (event == null) {
                event = new ArrayList<Pnml.Lpo.Event>();
            }
            return this.event;
        }

        /**
         * Gets the value of the lpoArc property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the lpoArc property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLpoArc().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Pnml.Lpo.LpoArc }
         * 
         * 
         */
        public List<Pnml.Lpo.LpoArc> getLpoArc() {
            if (lpoArc == null) {
                lpoArc = new ArrayList<Pnml.Lpo.LpoArc>();
            }
            return this.lpoArc;
        }

        /**
         * Obtiene el valor de la propiedad id.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Define el valor de la propiedad id.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Obtiene el valor de la propiedad type.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Define el valor de la propiedad type.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="name">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="graphics">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="offset">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                     &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="graphics">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="position">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "name",
            "graphics"
        })
        public static class Event implements Serializable{

            @XmlElement(required = true)
            protected Pnml.Lpo.Event.Name name;
            @XmlElement(required = true)
            protected Pnml.Lpo.Event.Graphics graphics;
            @XmlAttribute(name = "id")
            protected String id;

            /**
             * Obtiene el valor de la propiedad name.
             * 
             * @return
             *     possible object is
             *     {@link Pnml.Lpo.Event.Name }
             *     
             */
            public Pnml.Lpo.Event.Name getName() {
                return name;
            }

            /**
             * Define el valor de la propiedad name.
             * 
             * @param value
             *     allowed object is
             *     {@link Pnml.Lpo.Event.Name }
             *     
             */
            public void setName(Pnml.Lpo.Event.Name value) {
                this.name = value;
            }

            /**
             * Obtiene el valor de la propiedad graphics.
             * 
             * @return
             *     possible object is
             *     {@link Pnml.Lpo.Event.Graphics }
             *     
             */
            public Pnml.Lpo.Event.Graphics getGraphics() {
                return graphics;
            }

            /**
             * Define el valor de la propiedad graphics.
             * 
             * @param value
             *     allowed object is
             *     {@link Pnml.Lpo.Event.Graphics }
             *     
             */
            public void setGraphics(Pnml.Lpo.Event.Graphics value) {
                this.graphics = value;
            }

            /**
             * Obtiene el valor de la propiedad id.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Define el valor de la propiedad id.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="position">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "position"
            })
            public static class Graphics implements Serializable{

                @XmlElement(required = true)
                protected Pnml.Lpo.Event.Graphics.Position position;

                /**
                 * Obtiene el valor de la propiedad position.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Pnml.Lpo.Event.Graphics.Position }
                 *     
                 */
                public Pnml.Lpo.Event.Graphics.Position getPosition() {
                    return position;
                }

                /**
                 * Define el valor de la propiedad position.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Pnml.Lpo.Event.Graphics.Position }
                 *     
                 */
                public void setPosition(Pnml.Lpo.Event.Graphics.Position value) {
                    this.position = value;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Position implements Serializable{

                    @XmlAttribute(name = "x")
                    protected Integer x;
                    @XmlAttribute(name = "y")
                    protected Integer y;

                    /**
                     * Obtiene el valor de la propiedad x.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getX() {
                        return x;
                    }

                    /**
                     * Define el valor de la propiedad x.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setX(Integer value) {
                        this.x = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad y.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getY() {
                        return y;
                    }

                    /**
                     * Define el valor de la propiedad y.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setY(Integer value) {
                        this.y = value;
                    }

                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="graphics">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="offset">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                           &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value",
                "graphics"
            })
            public static class Name implements Serializable{

                @XmlElement(required = true)
                protected String value;
                @XmlElement(required = true)
                protected Pnml.Lpo.Event.Name.Graphics graphics;

                /**
                 * Obtiene el valor de la propiedad value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * Define el valor de la propiedad value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Obtiene el valor de la propiedad graphics.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Pnml.Lpo.Event.Name.Graphics }
                 *     
                 */
                public Pnml.Lpo.Event.Name.Graphics getGraphics() {
                    return graphics;
                }

                /**
                 * Define el valor de la propiedad graphics.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Pnml.Lpo.Event.Name.Graphics }
                 *     
                 */
                public void setGraphics(Pnml.Lpo.Event.Name.Graphics value) {
                    this.graphics = value;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="offset">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                 &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "offset"
                })
                public static class Graphics implements Serializable{

                    @XmlElement(required = true)
                    protected Pnml.Lpo.Event.Name.Graphics.Offset offset;

                    /**
                     * Obtiene el valor de la propiedad offset.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Pnml.Lpo.Event.Name.Graphics.Offset }
                     *     
                     */
                    public Pnml.Lpo.Event.Name.Graphics.Offset getOffset() {
                        return offset;
                    }

                    /**
                     * Define el valor de la propiedad offset.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Pnml.Lpo.Event.Name.Graphics.Offset }
                     *     
                     */
                    public void setOffset(Pnml.Lpo.Event.Name.Graphics.Offset value) {
                        this.offset = value;
                    }


                    /**
                     * <p>Clase Java para anonymous complex type.
                     * 
                     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class Offset implements Serializable{

                        @XmlAttribute(name = "x")
                        protected Integer x;
                        @XmlAttribute(name = "y")
                        protected Integer y;

                        /**
                         * Obtiene el valor de la propiedad x.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getX() {
                            return x;
                        }

                        /**
                         * Define el valor de la propiedad x.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setX(Integer value) {
                            this.x = value;
                        }

                        /**
                         * Obtiene el valor de la propiedad y.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getY() {
                            return y;
                        }

                        /**
                         * Define el valor de la propiedad y.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setY(Integer value) {
                            this.y = value;
                        }

                    }

                }

            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="graphics">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="userDrawn" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "graphics"
        })
        public static class LpoArc implements Serializable{

            @XmlElement(required = true)
            protected Pnml.Lpo.LpoArc.Graphics graphics;
            @XmlAttribute(name = "id")
            protected String id;
            @XmlAttribute(name = "source")
            protected String source;
            @XmlAttribute(name = "target")
            protected String target;

            /**
             * Obtiene el valor de la propiedad graphics.
             * 
             * @return
             *     possible object is
             *     {@link Pnml.Lpo.LpoArc.Graphics }
             *     
             */
            public Pnml.Lpo.LpoArc.Graphics getGraphics() {
                return graphics;
            }

            /**
             * Define el valor de la propiedad graphics.
             * 
             * @param value
             *     allowed object is
             *     {@link Pnml.Lpo.LpoArc.Graphics }
             *     
             */
            public void setGraphics(Pnml.Lpo.LpoArc.Graphics value) {
                this.graphics = value;
            }

            /**
             * Obtiene el valor de la propiedad id.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Define el valor de la propiedad id.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }

            /**
             * Obtiene el valor de la propiedad source.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSource() {
                return source;
            }

            /**
             * Define el valor de la propiedad source.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSource(String value) {
                this.source = value;
            }

            /**
             * Obtiene el valor de la propiedad target.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTarget() {
                return target;
            }

            /**
             * Define el valor de la propiedad target.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTarget(String value) {
                this.target = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="userDrawn" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Graphics implements Serializable{

                @XmlAttribute(name = "userDrawn")
                protected Boolean userDrawn;

                /**
                 * Obtiene el valor de la propiedad userDrawn.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public Boolean isUserDrawn() {
                    return userDrawn;
                }

                /**
                 * Define el valor de la propiedad userDrawn.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setUserDrawn(Boolean value) {
                    this.userDrawn = value;
                }

            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Name implements Serializable{

            @XmlElement(required = true)
            protected String value;

            /**
             * Obtiene el valor de la propiedad value.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Define el valor de la propiedad value.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}

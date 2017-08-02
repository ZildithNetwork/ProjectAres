package tc.oc.pgm.goals;

import org.jdom2.Element;
import tc.oc.api.docs.virtual.MatchDoc;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;

public class ProximityMetric {
    public enum Type {
        CLOSEST_PLAYER("closest player"),
        CLOSEST_BLOCK("closest block"),
        CLOSEST_KILL("closest kill");

        public final String description;

        Type(String description) {
            this.description = description;
        }
    }

    public final Type type;
    public final boolean horizontal;

    public ProximityMetric(Type type, boolean horizontal) {
        this.type = type;
        this.horizontal = horizontal;
    }

    public String name() {
        if(this.horizontal) {
            return this.type.name() + "_HORIZONTAL";
        } else {
            return this.type.name();
        }
    }

    public String description() {
        if(this.horizontal) {
            return this.type.description + " (horizontal)";
        } else {
            return this.type.description;
        }
    }

    public MatchDoc.TouchableGoal.Proximity.Metric apiValue() {
        return MatchDoc.TouchableGoal.Proximity.Metric.valueOf(name());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ProximityMetric)) return false;
        ProximityMetric that = (ProximityMetric) o;
        return this.type == that.type &&
               this.horizontal == that.horizontal;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (horizontal ? 1 : 0);
        return result;
    }

    public static ProximityMetric parse(Element el, ProximityMetric def) throws InvalidXMLException {
        return parse(el, "", def);
    }

    public static ProximityMetric parse(Element el, String prefix, ProximityMetric def) throws InvalidXMLException {
        if(!prefix.isEmpty()) prefix = prefix + "-";

        return new ProximityMetric(XMLUtils.parseEnum(Node.fromAttr(el, prefix + "proximity-metric"), ProximityMetric.Type.class, "proximity metric", def.type),
                                   XMLUtils.parseBoolean(el.getAttribute(prefix + "proximity-horizontal"), def.horizontal));
    }
}

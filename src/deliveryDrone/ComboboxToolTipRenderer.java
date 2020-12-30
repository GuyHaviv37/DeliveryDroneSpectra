package deliveryDrone;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class ComboboxToolTipRenderer extends DefaultListCellRenderer {
    List<String> tooltips;

    @SuppressWarnings("rawtypes")
	@Override
    public Component getListCellRendererComponent(JList list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {

        JComponent comp = (JComponent) super.getListCellRendererComponent(list,
                value, index, isSelected, cellHasFocus);

        if (-1 < index && null != value && null != tooltips) {
            list.setToolTipText(tooltips.get(index));
        }
        return comp;
    }

    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }
}

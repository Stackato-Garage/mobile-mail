package com.vaadin.demo.mobilemail.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.mobilemail.data.folder.Folder;

public class MobileMailContainer extends BeanItemContainer<AbstractPojo>
        implements Container.Hierarchical {

    private static final long serialVersionUID = 1L;

    public MobileMailContainer() {
        super(AbstractPojo.class);
    }

    public Collection<? extends AbstractPojo> getChildren(Object parent) {
        List<AbstractPojo> children = new ArrayList<AbstractPojo>();
        for (AbstractPojo pojo : getAllItemIds()) {
            if (pojo.getParent() == parent) {
                children.add(pojo);
            }
        }
        return children;
    }

    public Object getParent(Object itemId) {
        AbstractPojo pojo = (AbstractPojo) itemId;
        return pojo.getParent();
    }

    /**
     * Root items are Mailboxes so this returns the mailboxes
     */
    public Collection<?> rootItemIds() {
        List<AbstractPojo> pojos = getAllItemIds();
        if (pojos != null) {
            List<MailBox> mailboxes = new ArrayList<MailBox>();
            for (AbstractPojo pojo : pojos) {
                if (isRoot(pojo)) {
                    mailboxes.add((MailBox) pojo);
                }
            }
            return mailboxes;
        }
        return null;
    }

    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        if (itemId instanceof MailBox) {
            throw new UnsupportedOperationException(
                    "Mailboxes cannot have parents");
        } else if (itemId instanceof Message && newParentId instanceof MailBox) {
            throw new UnsupportedOperationException(
                    "Messages cannot be added to mailboxes");
        } else if (areChildrenAllowed(newParentId)) {
            AbstractPojo pojo = (AbstractPojo) itemId;
            pojo.setParent((AbstractPojo) newParentId);
            return true;
        }
        return false;
    }

    public boolean areChildrenAllowed(Object itemId) {
        return itemId instanceof MailBox || itemId instanceof Folder;
    }

    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not in use");
    }

    public boolean isRoot(Object itemId) {
        return itemId instanceof MailBox;
    }

    public boolean hasChildren(Object itemId) {
        if (itemId instanceof Message) {
            return false;
        }
        for (AbstractPojo pojo : getAllItemIds()) {
            if (pojo.getParent() == itemId) {
                return true;
            }
        }
        return false;
    }

    public void setFilter(Filter filter) {
        removeAllContainerFilters();
        if (filter != null) {
            addFilter(filter);
        }
    }
}

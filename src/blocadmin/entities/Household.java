package blocadmin.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Geanina
 */
public class Household implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private int buildingNr;
    private int appartmentNr;
    private String details;
    private int roomsNr;
    private int nrCurrentOccupants;
    private int totalCapacity;
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBuildingNr() {
        return buildingNr;
    }

    public void setBuildingNr(int buildingNr) {
        this.buildingNr = buildingNr;
    }

    public int getAppartmentNr() {
        return appartmentNr;
    }

    public void setAppartmentNr(int appartmentNr) {
        this.appartmentNr = appartmentNr;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getRoomsNr() {
        return roomsNr;
    }

    public void setRoomsNr(int roomsNr) {
        this.roomsNr = roomsNr;
    }

    public int getNrCurrentOccupants() {
        return nrCurrentOccupants;
    }

    public void setNrCurrentOccupants(int nrCurrentOccupants) {
        this.nrCurrentOccupants = nrCurrentOccupants;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + this.buildingNr;
        hash = 67 * hash + this.appartmentNr;
        hash = 67 * hash + Objects.hashCode(this.details);
        hash = 67 * hash + this.roomsNr;
        hash = 67 * hash + this.nrCurrentOccupants;
        hash = 67 * hash + this.totalCapacity;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Household other = (Household) obj;
        if (this.buildingNr != other.buildingNr) {
            return false;
        }
        if (this.appartmentNr != other.appartmentNr) {
            return false;
        }
        if (this.roomsNr != other.roomsNr) {
            return false;
        }
        if (this.nrCurrentOccupants != other.nrCurrentOccupants) {
            return false;
        }
        if (this.totalCapacity != other.totalCapacity) {
            return false;
        }
        if (!Objects.equals(this.details, other.details)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Household{" + "id=" + id + ", buildingNr=" + buildingNr + ", appartmentNr=" + appartmentNr + ", details=" + details + ", roomsNr=" + roomsNr + ", nrCurrentOccupants=" + nrCurrentOccupants + ", totalCapacity=" + totalCapacity + ", owner=" + owner + '}';
    }
}

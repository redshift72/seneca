package it.enea.lecop.eca.model;



import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public abstract class AutoIdeable {

	
	  

	 

	   public abstract Number getId();
		
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getId() == null) ? 0 : getId().intValue());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AutoIdeable other = (AutoIdeable) obj;
			if (getId() == null) {
				if (other.getId() != null)
					return false;
			} else if (!(getId() == other.getId()))
				return false;
			return true;
		}

}

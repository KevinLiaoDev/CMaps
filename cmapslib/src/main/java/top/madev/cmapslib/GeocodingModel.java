package top.madev.cmapslib;

import java.util.List;

/**
 * Created by lk on 2016/09/12.
 */
public class GeocodingModel
{
	private String status;
	private List<Result> results;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public class Result {
		private String[] types;
		private String formatted_address;
		private Geometry geometry;

		public Geometry getGeometry() {
			return geometry;
		}

		public void setGeometry(Geometry geometry) {
			this.geometry = geometry;
		}

		public String getFormatted_address() {
			return formatted_address;
		}

		public void setFormatted_address(String formattedAddress) {
			formatted_address = formattedAddress;
		}

		public String[] getTypes() {
			return types;
		}

		public void setTypes(String[] types) {
			this.types = types;
		}
	}

	public class Geometry{
		private MyGeoLocation location;

		public MyGeoLocation getLocation() {
			return location;
		}

		public void setLocation(MyGeoLocation location) {
			this.location = location;
		}
	}

	public class MyGeoLocation {
		private double lat;
		private double lng;

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLng() {
			return lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}
	}
}

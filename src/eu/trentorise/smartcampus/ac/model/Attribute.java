/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.trentorise.smartcampus.ac.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author raman
 */
public class Attribute {

	private Authority authority;

	private String key;

	private String value;

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Attribute other = (Attribute) obj;
		if (this.authority != other.authority
				&& (this.authority == null || !this.authority
						.equals(other.authority))) {
			return false;
		}
		if ((this.key == null) ? (other.key != null) : !this.key
				.equals(other.key)) {
			return false;
		}
		if ((this.value == null) ? (other.value != null) : !this.value
				.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash
				+ (this.authority != null ? this.authority.hashCode() : 0);
		hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
		hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Attribute{authority=" + authority + ", key=" + key + ", value="
				+ value + '}';
	}

	/**
	 * @param json
	 * @return
	 */
	public static Attribute valueOf(JSONObject json) {
		if (json == null) return null;
		Attribute a = new Attribute();
		try {
			a.setKey(json.getString("key"));
			a.setValue(json.getString("value"));
			a.setAuthority(Authority.valueOf(json.getJSONObject("authority")));
			return a;
		} catch (JSONException e) {
			return null;
		}
	}

}

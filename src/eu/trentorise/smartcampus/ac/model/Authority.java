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
public class Authority {

	private Long id;
	private String name;
	private String redirectUrl;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Authority other = (Authority) obj;
		if ((this.name == null) ? (other.name != null) : !this.name
				.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Authority{id=" + id + ", name=" + name + ", url=" + redirectUrl
				+ '}';
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public static Authority valueOf(JSONObject json) {
		if (json == null) return null;
		Authority a = new Authority();
		try {
			a.setId(json.getLong("id"));
			a.setName(json.getString("name"));
			a.setRedirectUrl(json.getString("redirectUrl"));
			return a;
		} catch (JSONException e) {
			return null;
		}
	}

}

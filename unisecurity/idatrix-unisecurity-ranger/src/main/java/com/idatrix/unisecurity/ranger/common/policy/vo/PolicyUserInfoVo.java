package com.idatrix.unisecurity.ranger.common.policy.vo;

import java.util.List;

public class PolicyUserInfoVo {
	
	private String startIndex;

	private String pageSize;

	private String totalCount;

	private String resultSize;

	private String sortType;

	private String sortBy;

	private String queryTimeMS;
	
	private List<VXUsers> vXUsers;
	
	private List<PolicyInfoVO> policies;
	
	private List<VXGroups> vXGroups;
	
    public List<PolicyInfoVO> getPolicies() {
        return policies;
    }

    
    public void setPolicies(List<PolicyInfoVO> policies) {
        this.policies = policies;
    }

    public String getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(String startIndex) {
		this.startIndex = startIndex;
	}

	public String getPageSize() {
		return pageSize;
	}


	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}


	public String getTotalCount() {
		return totalCount;
	}





	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}





	public String getResultSize() {
		return resultSize;
	}



	public void setResultSize(String resultSize) {
		this.resultSize = resultSize;
	}





	public String getSortType() {
		return sortType;
	}



	public void setSortType(String sortType) {
		this.sortType = sortType;
	}





	public String getSortBy() {
		return sortBy;
	}



	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}





	public String getQueryTimeMS() {
		return queryTimeMS;
	}





	public void setQueryTimeMS(String queryTimeMS) {
		this.queryTimeMS = queryTimeMS;
	}





	public List<VXUsers> getvXUsers() {
		return vXUsers;
	}



	public void setvXUsers(List<VXUsers> vXUsers) {
		this.vXUsers = vXUsers;
	}
	
    public List<VXGroups> getvXGroups() {
        return vXGroups;
    }


    
    public void setvXGroups(List<VXGroups> vXGroups) {
        this.vXGroups = vXGroups;
    }



    public static class VXGroups{
	    private int id;
	    private String createDate;
	    private String updateDate;
	    private String owner;
	    private String updateBy;
	    private String name;
	    private String descriptioin;
	    private int groupType;
	    private int groupSource;
	    private String isVisible;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getCreateDate() {
            return createDate;
        }
        
        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
        
        public String getUpdateDate() {
            return updateDate;
        }
        
        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
        
        public String getOwner() {
            return owner;
        }
        
        public void setOwner(String owner) {
            this.owner = owner;
        }
        
        public String getUpdateBy() {
            return updateBy;
        }
        
        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescriptioin() {
            return descriptioin;
        }
        
        public void setDescriptioin(String descriptioin) {
            this.descriptioin = descriptioin;
        }
        
        public int getGroupType() {
            return groupType;
        }
        
        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }
        
        public int getGroupSource() {
            return groupSource;
        }
        
        public void setGroupSource(int groupSource) {
            this.groupSource = groupSource;
        }
        
        public String getIsVisible() {
            return isVisible;
        }
        
        public void setIsVisible(String isVisible) {
            this.isVisible = isVisible;
        }
	    
	    
	}





	public static class VXUsers {
		private String id;
		private String createDate;
		private String updateDate;
		private String owner;
		private String updatedBy;
		private String name;
		private String firstName;
		private String lastName;
		private String password;
		private String description;
		private List<String> groupIdList;
		private List<String> groupNameList;
		private String status;
		private String isVisible;
		private String userSource;
		private List<String> userRoleList;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getUpdateDate() {
			return updateDate;
		}
		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}
		public String getOwner() {
			return owner;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
		public String getUpdatedBy() {
			return updatedBy;
		}
		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public List<String> getGroupIdList() {
			return groupIdList;
		}
		public void setGroupIdList(List<String> groupIdList) {
			this.groupIdList = groupIdList;
		}
		public List<String> getGroupNameList() {
			return groupNameList;
		}
		public void setGroupNameList(List<String> groupNameList) {
			this.groupNameList = groupNameList;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getIsVisible() {
			return isVisible;
		}
		public void setIsVisible(String isVisible) {
			this.isVisible = isVisible;
		}
		public String getUserSource() {
			return userSource;
		}
		public void setUserSource(String userSource) {
			this.userSource = userSource;
		}
		public List<String> getUserRoleList() {
			return userRoleList;
		}
		public void setUserRoleList(List<String> userRoleList) {
			this.userRoleList = userRoleList;
		}
        @Override
        public String toString() {
            return "VXUsers [id=" + id + ", createDate=" + createDate + ", updateDate=" + updateDate + ", owner="
                    + owner + ", updatedBy=" + updatedBy + ", name=" + name + ", firstName=" + firstName
                    + ", lastName=" + lastName + ", password=" + password + ", description=" + description
                    + ", groupIdList=" + groupIdList + ", groupNameList=" + groupNameList + ", status=" + status
                    + ", isVisible=" + isVisible + ", userSource=" + userSource + ", userRoleList=" + userRoleList
                    + "]";
        }
		
		
	}


    @Override
    public String toString() {
        return "PolicyUserInfoVo [startIndex=" + startIndex + ", pageSize=" + pageSize + ", totalCount=" + totalCount
                + ", resultSize=" + resultSize + ", sortType=" + sortType + ", sortBy=" + sortBy + ", queryTimeMS="
                + queryTimeMS + ", vXUsers=" + vXUsers + ", policies=" + policies + "]";
    }



}

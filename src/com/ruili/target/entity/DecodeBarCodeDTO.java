package com.ruili.target.entity;

public class DecodeBarCodeDTO extends ResponseDTO {

	private IndexID data;
	
	public void setData(IndexID data) {
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}
	
	public static class IndexID {
		private int big_index_id;

		public int getBig_index_id() {
			return big_index_id;
		}

		public void setBig_index_id(int big_index_id) {
			this.big_index_id = big_index_id;
		}

		
	}

}

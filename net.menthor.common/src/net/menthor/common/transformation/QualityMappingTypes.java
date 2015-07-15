package net.menthor.common.transformation;

public enum QualityMappingTypes {
	hideQuality{
		@Override
		public String toString() {
			return "Ignore (hide) the quality";
		}
	},
	maintainQuality{
		@Override
		public String toString() {
			return "Maintain the quality as a class";
		}
	}
}
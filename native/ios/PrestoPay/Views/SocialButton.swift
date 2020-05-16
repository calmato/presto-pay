import SwiftUI

struct SocialButton: View {
  var name: String
  var image: String

  var body: some View {
    Button(action: {}) {
      HStack {
        Image(self.image)
          .renderingMode(.original)
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(height: 24)
        Text("\(self.name)でサインイン")
          .foregroundColor(.black)
          .frame(width: 256)
      }
        .frame(width: 348, height: 36)
        .overlay(
          RoundedRectangle(cornerRadius: 3)
            .stroke(Color.gray, lineWidth: 1)
        )
    }
  }
}

struct SocialButton_Previews: PreviewProvider {
  static var previews: some View {
    SocialButton(name: "Google", image: "google")
  }
}

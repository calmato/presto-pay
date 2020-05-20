import SwiftUI

struct CustomSecureField: View {
  @Binding var text: String
  @Binding var value: String
  @Binding var image: String

  var body: some View {
    HStack {
      if image != "" {
        Image(self.image)
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(height: 28)
      }
      
      SecureField(self.text, text: $value)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()
    }
  }
}

struct CustomSecureField_Previews: PreviewProvider {
  static var previews: some View {
    CustomSecureField(
      text: .constant("Password"),
      value: .constant(""),
      image: .constant("password")
    )
  }
}

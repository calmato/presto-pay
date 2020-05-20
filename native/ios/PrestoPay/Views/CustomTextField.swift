import SwiftUI

struct CustomTextField: View {
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
      
      TextField(self.text, text: $value)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()
    }
  }
}

struct CustomTextField_Previews: PreviewProvider {
  static var previews: some View {
    CustomTextField(
      text: .constant("Email"),
      value: .constant(""),
      image: .constant("email")
    )
  }
}

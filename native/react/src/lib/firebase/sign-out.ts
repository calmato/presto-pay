import firebase from "./config";

export default async function signOut(): Promise<void> {
  await firebase.auth().signOut();
}
